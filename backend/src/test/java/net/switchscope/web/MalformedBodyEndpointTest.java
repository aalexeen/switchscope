package net.switchscope.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.switchscope.AbstractContextTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A body the server cannot read is the client's mistake and must be answered as one.
 * <p>
 * Two paths lead to it and they used to disagree. A route with a typed {@code @RequestBody} let
 * Spring fail with {@code HttpMessageNotReadableException}, which no entry in the status map
 * matched, so the caller was told {@code 500 Application error} about their own payload. A route
 * that reads the body as a string reported the same mistake through {@code JsonPayload} as 422.
 * Both now answer 400, which is what a request that cannot be parsed at all deserves - 422 stays
 * for a body that was read and then rejected.
 */
@SpringBootTest(properties = {
        "spring.testcontainers.enabled=false",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration"
})
@AutoConfigureMockMvc
class MalformedBodyEndpointTest extends AbstractContextTest {

    private static final String LOCATION_TYPES = "/api/catalogs/location-types";
    private static final String LOCATIONS = "/api/locations";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("unparseable JSON on a typed body is 400, not 500")
    void unparseableBodyOnATypedRouteIsBadRequest() throws Exception {
        mockMvc.perform(post(LOCATION_TYPES)
                        .with(httpBasic("admin@gmail.com", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"displayName\": "))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("an empty body on a typed body is 400, not 500")
    void emptyBodyOnATypedRouteIsBadRequest() throws Exception {
        mockMvc.perform(post(LOCATION_TYPES)
                        .with(httpBasic("admin@gmail.com", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("unparseable JSON on a raw body is the same 400 as on a typed one")
    void unparseableBodyOnARawRouteIsBadRequest() throws Exception {
        mockMvc.perform(put(LOCATIONS + "/" + anyLocationId())
                        .with(httpBasic("admin@gmail.com", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"address\": "))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("a JSON array where an object is expected is 400 as well")
    void anArrayWhereAnObjectIsExpectedIsBadRequest() throws Exception {
        mockMvc.perform(put(LOCATIONS + "/" + anyLocationId())
                        .with(httpBasic("admin@gmail.com", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1, 2, 3]"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("a value of the wrong type is 400 - the body was read, the field could not be")
    void aFieldThatDoesNotBindIsBadRequest() throws Exception {
        mockMvc.perform(put(LOCATIONS + "/" + anyLocationId())
                        .with(httpBasic("admin@gmail.com", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"floorNumber\": \"third\"}"))
                .andExpect(status().isBadRequest());
    }

    private String anyLocationId() throws Exception {
        String all = mockMvc.perform(get(LOCATIONS)
                        .with(httpBasic("admin@gmail.com", "admin"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var locations = objectMapper.readTree(all);
        assertThat(locations).as("the seeded locations are the fixture this test stands on").isNotEmpty();
        return locations.get(0).get("id").asText();
    }
}
