package net.switchscope.security.policy;

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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Clearing a field over HTTP, on a route that until now could not do it at all.
 * <p>
 * {@code /api/locations} is served by a subclass of {@code AbstractCrudController}, which took a
 * bound DTO and therefore could not tell {@code {"address": null}} from a payload that never
 * mentioned {@code address}. That was the reason the field-access layer had to stay unapplied:
 * half the API able to clear a field and half not is worse than neither. This test is the claim
 * that the half is gone - so it deliberately runs against one of the nine, not against the
 * polymorphic controllers that always read raw JSON.
 * <p>
 * Real users out of the database rather than {@code @WithMockUser}, because the policy is chosen by
 * {@code UpdatePolicyResolver} from the {@code AuthUser} principal: a mock principal is not one, so
 * every caller would be a USER and the admin half of the behaviour would go untested.
 */
@SpringBootTest(properties = {
        "spring.testcontainers.enabled=false",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration"
})
@AutoConfigureMockMvc
class NullificationEndpointTest extends AbstractContextTest {

    private static final String LOCATIONS = "/api/locations";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("an explicit null clears the field; a field the payload omits keeps its value")
    void explicitNullClearsAndOmissionDoesNot() throws Exception {
        UUID id = createLocation("with-address", "12 Somewhere Street");
        try {
            putAs(id, """
                    {"floorNumber": 3}""", "admin@gmail.com", "admin").andExpect(status().isOk());
            assertThat(read(id).get("address").asText())
                    .as("address was not mentioned, so it must survive untouched - which is the"
                            + " half that already worked and must keep working")
                    .isEqualTo("12 Somewhere Street");

            putAs(id, """
                    {"address": null}""", "admin@gmail.com", "admin").andExpect(status().isOk());
            assertThat(read(id).get("address").isNull())
                    .as("address was sent as null by a caller allowed to clear it")
                    .isTrue();
            assertThat(read(id).get("floorNumber").asInt())
                    .as("clearing one field must not disturb another")
                    .isEqualTo(3);
        } finally {
            remove(id);
        }
    }

    @Test
    @DisplayName("a caller whose policy does not allow clearing is refused, and nothing changes")
    void refusesClearingWithoutThePolicy() throws Exception {
        UUID id = createLocation("user-cannot-clear", "12 Somewhere Street");
        try {
            putAs(id, """
                    {"description": "set by the test"}""", "admin@gmail.com", "admin")
                    .andExpect(status().isOk());

            putAs(id, """
                    {"description": null}""", "user@gmail.com", "password")
                    .andExpect(status().isForbidden());
            assertThat(read(id).get("description").asText())
                    .as("description is ADMIN_NULLABLE, so a USER asking to clear it is refused -"
                            + " and a refused request must not have applied half of itself")
                    .isEqualTo("set by the test");

            putAs(id, """
                    {"description": null}""", "admin@gmail.com", "admin").andExpect(status().isOk());
            assertThat(read(id).get("description").isNull())
                    .as("the same request from an admin goes through, so the refusal above is"
                            + " about the policy and not about the field being unclearable")
                    .isTrue();
        } finally {
            remove(id);
        }
    }

    @Test
    @DisplayName("a field nobody may clear is refused even for an admin")
    void refusesClearingARequiredField() throws Exception {
        UUID id = createLocation("cannot-clear-type", "12 Somewhere Street");
        try {
            putAs(id, """
                    {"typeId": null}""", "admin@gmail.com", "admin")
                    .andExpect(status().isForbidden());
            assertThat(read(id).get("typeId").isNull())
                    .as("the type link is what makes the row valid; the refusal happens before any"
                            + " write, so it is still there")
                    .isFalse();
        } finally {
            remove(id);
        }
    }

    @Test
    @DisplayName("a whole object echoed back unchanged is accepted, by a USER too")
    void wholeObjectRoundTripIsAccepted() throws Exception {
        UUID id = createLocation("round-trip", "12 Somewhere Street");
        try {
            com.fasterxml.jackson.databind.node.ObjectNode whole =
                    (com.fasterxml.jackson.databind.node.ObjectNode) read(id);
            whole.remove("createdAt");
            whole.remove("updatedAt");

            putAs(id, objectMapper.writeValueAsString(whole), "user@gmail.com", "password")
                    .andExpect(status().isOk());

            assertThat(read(id).get("address").asText())
                    .as("this is what the detail view actually sends: the object as fetched, empty"
                            + " optionals and all. Once nulls started being applied, a DTO whose"
                            + " fields nobody had given a level would have defaulted to"
                            + " admin-clearable and locked every USER out of saving anything")
                    .isEqualTo("12 Somewhere Street");
        } finally {
            remove(id);
        }
    }

    private UUID createLocation(String name, String address) throws Exception {
        UUID typeId = UUID.fromString(firstLocationTypeId());
        String body = objectMapper.writeValueAsString(java.util.Map.of(
                "name", name + "-" + UUID.randomUUID(),
                "typeId", typeId.toString(),
                "address", address));
        String created = mockMvc.perform(post(LOCATIONS)
                        .with(httpBasic("admin@gmail.com", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return UUID.fromString(objectMapper.readTree(created).get("id").asText());
    }

    private String firstLocationTypeId() throws Exception {
        String types = mockMvc.perform(get("/api/catalogs/location-types")
                        .with(httpBasic("admin@gmail.com", "admin"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode all = objectMapper.readTree(types);
        assertThat(all).as("the seeded catalog is the fixture this test stands on").isNotEmpty();
        return all.get(0).get("id").asText();
    }

    private org.springframework.test.web.servlet.ResultActions putAs(
            UUID id, String body, String user, String password) throws Exception {
        return mockMvc.perform(put(LOCATIONS + "/" + id)
                .with(httpBasic(user, password))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private JsonNode read(UUID id) throws Exception {
        String json = mockMvc.perform(get(LOCATIONS + "/" + id)
                        .with(httpBasic("admin@gmail.com", "admin"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(json);
    }

    private void remove(UUID id) throws Exception {
        mockMvc.perform(delete(LOCATIONS + "/" + id).with(httpBasic("admin@gmail.com", "admin")));
    }
}
