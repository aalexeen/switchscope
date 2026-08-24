package net.switchscope.web.catalog;

import com.fasterxml.jackson.databind.JsonNode;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * An association that travels as a list of ids, through an endpoint that used to drop it.
 * <p>
 * {@code allowedChildTypeIds} is one of the fields whose mapper turns the stored association into
 * ids on the way out and ignores the ids on the way in, with nothing in between to resolve them.
 * The request was answered 200 with the ids the caller had sent - read back from the DTO, not from
 * the database - so the silence was hard to see from the client side.
 */
@SpringBootTest(properties = {
        "spring.testcontainers.enabled=false",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration"
})
@AutoConfigureMockMvc
@Transactional
class AllowedChildTypesEndpointTest extends AbstractContextTest {

    private static final String TYPES = "/api/catalogs/location-types";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("a create that names an allowed child type stores it")
    void createStoresTheAssociation() throws Exception {
        UUID child = anyExistingTypeId();

        UUID created = create("""
                {"code": "TEST-CHILD-1", "name": "test type one", "displayName": "Test Type One", "hierarchyLevel": 9,
                 "allowedChildTypeIds": ["%s"]}""".formatted(child));

        assertThat(idsOf(read(created))).containsExactly(child.toString());
    }

    @Test
    @DisplayName("an update replaces the set, an empty one clears it, an absent one keeps it")
    void updateReplacesClearsAndKeeps() throws Exception {
        UUID child = anyExistingTypeId();
        UUID created = create("""
                {"code": "TEST-CHILD-2", "name": "test type two", "displayName": "Test Type Two", "hierarchyLevel": 9}""");

        update(created, """
                {"allowedChildTypeIds": ["%s"]}""".formatted(child));
        assertThat(idsOf(read(created))).containsExactly(child.toString());

        update(created, """
                {"displayName": "Renamed"}""");
        assertThat(idsOf(read(created)))
                .as("the payload did not mention the field, so the association stands")
                .containsExactly(child.toString());

        update(created, """
                {"allowedChildTypeIds": []}""");
        assertThat(idsOf(read(created))).isEmpty();
    }

    @Test
    @DisplayName("an id that does not exist is refused rather than quietly dropped")
    void unknownChildIdIsRefused() throws Exception {
        UUID created = create("""
                {"code": "TEST-CHILD-3", "name": "test type three", "displayName": "Test Type Three", "hierarchyLevel": 9}""");

        mockMvc.perform(put(TYPES + "/" + created)
                        .with(httpBasic("admin@gmail.com", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"allowedChildTypeIds": ["00000000-0000-0000-0000-0000000000ff"]}"""))
                .andExpect(status().isNotFound());
    }

    private UUID anyExistingTypeId() throws Exception {
        JsonNode all = objectMapper.readTree(mockMvc.perform(get(TYPES)
                        .with(httpBasic("admin@gmail.com", "admin")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());
        return UUID.fromString(all.get(0).get("id").asText());
    }

    private UUID create(String body) throws Exception {
        String json = mockMvc.perform(post(TYPES)
                        .with(httpBasic("admin@gmail.com", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return UUID.fromString(objectMapper.readTree(json).get("id").asText());
    }

    private void update(UUID id, String body) throws Exception {
        mockMvc.perform(put(TYPES + "/" + id)
                        .with(httpBasic("admin@gmail.com", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    private JsonNode read(UUID id) throws Exception {
        return objectMapper.readTree(mockMvc.perform(get(TYPES + "/" + id)
                        .with(httpBasic("admin@gmail.com", "admin")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());
    }

    private static List<String> idsOf(JsonNode node) {
        List<String> ids = new ArrayList<>();
        node.get("allowedChildTypeIds").forEach(id -> ids.add(id.asText()));
        return ids;
    }
}
