package net.switchscope.web.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.switchscope.AbstractContextTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Updating one's own profile, which two identity comparisons on {@code UUID} made impossible.
 * <p>
 * {@code ValidationUtil.assureIdConsistent} compared the body's id to the path's with {@code !=},
 * so any {@code PUT} that carried an id was refused; {@code UniqueMailValidator} compared the
 * stored id to the caller's the same way, so its "it is ok, if update ourselves" escape never
 * fired and keeping one's own address read as taking someone else's. Two different symptoms, one
 * mistake: {@code UUID} instances are not interned, and these ones arrive from JSON and from the
 * database, never as the same object.
 */
@SpringBootTest(properties = {
        "spring.testcontainers.enabled=false",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration"
})
@AutoConfigureMockMvc
@Transactional
class ProfileUpdateEndpointTest extends AbstractContextTest {

    private static final String PROFILE = "/api/profile";
    private static final String USER = "user@gmail.com";
    private static final String PASSWORD = "password";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("a caller may send their own id and their own address back")
    void updateWithOwnIdAndOwnEmail() throws Exception {
        UUID id = ownId();

        mockMvc.perform(profilePut(body(id, "Renamed User", USER)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("the id in the body still has to be the caller's own")
    void updateWithForeignIdIsRefused() throws Exception {
        mockMvc.perform(profilePut(body(UUID.fromString("00000000-0000-0000-0000-0000000000ff"),
                "Renamed User", USER)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("an address that belongs to somebody else is still refused")
    void updateToAnotherUsersEmailIsRefused() throws Exception {
        mockMvc.perform(profilePut(body(ownId(), "Renamed User", "admin@gmail.com")))
                .andExpect(status().isUnprocessableEntity());
    }

    private UUID ownId() throws Exception {
        String json = mockMvc.perform(get(PROFILE).with(httpBasic(USER, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return UUID.fromString(objectMapper.readTree(json).get("id").asText());
    }

    private static String body(UUID id, String name, String email) {
        return """
                {"id": "%s", "name": "%s", "email": "%s", "password": "%s"}"""
                .formatted(id, name, email, PASSWORD);
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder profilePut(String body) {
        return put(PROFILE)
                .with(httpBasic(USER, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);
    }
}
