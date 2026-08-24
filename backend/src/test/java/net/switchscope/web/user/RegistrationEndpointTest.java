package net.switchscope.web.user;

import net.switchscope.AbstractContextTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Registering without an account, which is what a registration endpoint is for.
 * <p>
 * {@code POST /api/profile} sat behind {@code requestMatchers("/api/**").authenticated()}, so the
 * only way to get an account was to already have one - while the frontend has offered a
 * {@code /register} route from the login screen all along.
 * <p>
 * Runs in a rolled-back transaction: the tests really do create a user, and the database they run
 * against is the development one.
 */
@SpringBootTest(properties = {
        "spring.testcontainers.enabled=false",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration"
})
@AutoConfigureMockMvc
@Transactional
class RegistrationEndpointTest extends AbstractContextTest {

    private static final String PROFILE = "/api/profile";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("an unauthenticated caller can register")
    void anonymousRegistrationSucceeds() throws Exception {
        mockMvc.perform(register("newcomer@example.com", "secret42"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("and can then authenticate with what they registered")
    void registeredUserCanAuthenticate() throws Exception {
        mockMvc.perform(register("newcomer2@example.com", "secret42"))
                .andExpect(status().isCreated());

        mockMvc.perform(get(PROFILE).with(httpBasic("newcomer2@example.com", "secret42")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("an e-mail that is already taken is refused, anonymous caller or not")
    void duplicateEmailIsRefused() throws Exception {
        mockMvc.perform(register("admin@gmail.com", "secret42"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString(UniqueMailValidator.EXCEPTION_DUPLICATE_EMAIL)));
    }

    @Test
    @DisplayName("only POST is open: reading a profile still needs an account")
    void theOpeningIsLimitedToRegistration() throws Exception {
        mockMvc.perform(get(PROFILE))
                .andExpect(status().isUnauthorized());
    }

    private static org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder register(
            String email, String password) {
        return post(PROFILE)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name": "Newcomer", "email": "%s", "password": "%s"}""".formatted(email, password));
    }
}
