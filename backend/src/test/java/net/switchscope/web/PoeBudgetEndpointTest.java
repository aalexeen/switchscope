package net.switchscope.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * That the PoE budget defect is reachable from outside, and by an ordinary request rather than a
 * contrived one.
 * <p>
 * {@code PoeBudgetTest} shows the arithmetic; this shows what it costs. Creating a port with PoE
 * switched on and no reading yet is what the API is for - {@code poePowerWatts} is the current
 * consumption and nobody has to supply one - and until this was fixed that single POST made
 * <em>every</em> read of the switch answer 500, including the list the frontend loads.
 */
@SpringBootTest(properties = {
        "spring.testcontainers.enabled=false",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration"
})
@AutoConfigureMockMvc
class PoeBudgetEndpointTest {

    private static final String SWITCHES = "/api/devices/switches";
    private static final String PORTS = "/api/ports";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("a PoE port with no reading yet does not make its switch unreadable")
    void poePortWithoutAReadingKeepsTheSwitchReadable() throws Exception {
        JsonNode aSwitch = aSwitchWithAPoeBudget();
        UUID switchId = UUID.fromString(aSwitch.get("id").asText());
        double budgetBefore = read(SWITCHES + "/" + switchId).get("availablePoeBudget").asDouble();

        UUID portId = createSilentPoePort(switchId);
        try {
            JsonNode after = read(SWITCHES + "/" + switchId);

            assertThat(after.get("availablePoeBudget").asDouble())
                    .as("the new port draws nothing anyone has measured, so the remaining budget is"
                            + " what it was - and, more to the point, the switch still answers 200")
                    .isEqualTo(budgetBefore);
        } finally {
            mockMvc.perform(delete(PORTS + "/" + portId).with(httpBasic("admin@gmail.com", "admin")));
        }
    }

    /** A port as the API allows one to be created: PoE on, consumption not stated. */
    private UUID createSilentPoePort(UUID deviceId) throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("portType", "ETHERNET");
        body.put("deviceId", deviceId.toString());
        body.put("portNumber", 9999);
        body.put("name", "poe-budget-test-" + UUID.randomUUID());
        body.put("poeEnabled", true);
        // Nothing else: the six NOT NULL fields the schema calls optional are supplied by the port
        // itself now (Port.supplyMissingDefaults). This method used to send five of them to get
        // past a 409, which meant the test was creating a port no client would have to.

        String created = mockMvc.perform(post(PORTS)
                        .with(httpBasic("admin@gmail.com", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return UUID.fromString(objectMapper.readTree(created).get("id").asText());
    }

    private JsonNode aSwitchWithAPoeBudget() throws Exception {
        for (JsonNode sw : objectMapper.readTree(getBody(SWITCHES))) {
            JsonNode budget = sw.get("poeBudgetWatts");
            if (budget != null && !budget.isNull() && budget.asInt() > 0) {
                return sw;
            }
        }
        throw new AssertionError("no seeded switch with a PoE budget; without one the sum this test"
                + " is about is never reached");
    }

    private JsonNode read(String url) throws Exception {
        return objectMapper.readTree(getBody(url));
    }

    private String getBody(String url) throws Exception {
        return mockMvc.perform(get(url)
                        .with(httpBasic("admin@gmail.com", "admin"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }
}
