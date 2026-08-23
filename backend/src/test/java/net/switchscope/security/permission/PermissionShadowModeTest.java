package net.switchscope.security.permission;

import net.switchscope.AbstractContextTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The rollout mechanism itself: shadow refuses nothing, and one domain can be promoted ahead of the
 * rest.
 * <p>
 * This is the shape the application actually ships in - {@code mode: SHADOW} - so leaving it
 * untested would mean the only exercised path is the one nobody runs. The two assertions are the
 * two halves of the promise: an unpromoted domain cannot refuse anyone no matter how wrong the
 * grant table is, and a promoted one refuses for real without the others moving. That second half
 * is also the rollback: take {@code catalog} out of the list and it drops straight back to shadow,
 * with no rebuild and no code change.
 */
@SpringBootTest(properties = {
        "spring.testcontainers.enabled=false",
        "switchscope.security.permission.mode=SHADOW",
        "switchscope.security.permission.enforce-domains=catalog",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration"
})
@AutoConfigureMockMvc
class PermissionShadowModeTest extends AbstractContextTest {

    private static final UUID ABSENT = UUID.fromString("00000000-0000-4000-8000-0000000000ff");

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("a domain still in shadow refuses nobody, however wrong the grants are")
    @WithMockUser(authorities = "nothing.at:all")
    void shadowDomainRefusesNobody() throws Exception {
        int status = mockMvc.perform(delete("/api/housing/racks/" + ABSENT))
                .andReturn().getResponse().getStatus();
        assertThat(status)
                .as("component is not in enforce-domains, so the decision is only recorded")
                .isNotEqualTo(403);
    }

    @Test
    @DisplayName("a promoted domain refuses for real while the rest stay in shadow")
    @WithMockUser(authorities = "nothing.at:all")
    void promotedDomainRefuses() throws Exception {
        mockMvc.perform(get("/api/catalogs/component-types").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("a promoted domain still lets through whoever holds the permission")
    @WithMockUser(authorities = "catalog.component-type:read")
    void promotedDomainAllowsTheHolder() throws Exception {
        mockMvc.perform(get("/api/catalogs/component-types").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
