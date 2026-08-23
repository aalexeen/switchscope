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
 * The test the whole of stage 2 rests on: that the key enforcement looks up is the key the scan
 * recorded.
 *
 * <h2>Why this is not paranoia</h2>
 * The subclasses of {@code AbstractCrudController} are generic, and the compiler emits <em>bridge
 * methods</em> for the operations they override with a narrowed type - {@code javap -p
 * RackController} shows both {@code RackTo get(UUID)} and a synthetic {@code BaseTo get(UUID)}.
 * Operations they do not override, such as {@code create}, have no bridge and arrive as the base
 * class's own {@code Method}. So there are two different method identities in play, and which one
 * an AOP interceptor sees is not obvious from reading the source. Get it wrong and every catalog
 * route answers 403 to users who hold the permission - a failure that looks like a broken grant
 * table and is not.
 *
 * <h2>Why the assertions are "not 403"</h2>
 * Whether the request then succeeds depends on fixtures this test deliberately does not create.
 * A lookup miss is what produces 403 under {@code ENFORCE}, so "anything other than 403 with the
 * permission, and 403 without it" isolates the question being asked. Both method shapes are
 * covered, plus a controller that declares its own methods, because they fail independently.
 * <p>
 * The write probes are {@code DELETE} rather than {@code POST} on purpose: argument resolution runs
 * before the authorization interceptor, so a body that cannot be deserialised never reaches the
 * check and the test would prove nothing about it either way.
 */
@SpringBootTest(properties = {
        "spring.testcontainers.enabled=false",
        "switchscope.security.permission.mode=ENFORCE",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration"
})
@AutoConfigureMockMvc
class PermissionEnforcementTest extends AbstractContextTest {

    /** An id nothing is seeded with, so the outcome never depends on fixture data. */
    private static final UUID ABSENT = UUID.fromString("00000000-0000-4000-8000-0000000000ff");

    private static final String RACKS = "/api/housing/racks";
    private static final String COMPONENT_TYPES = "/api/catalogs/component-types";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("inherited-and-overridden method: the permission is honoured despite the bridge method")
    @WithMockUser(authorities = "component.rack:read")
    void overriddenMethodResolvesItsPermission() throws Exception {
        int status = mockMvc.perform(get(RACKS + "/" + ABSENT).accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getStatus();
        assertThat(status)
                .as("RackController#get is overridden with a narrowed return type, so a synthetic"
                        + " bridge method exists; a 403 here means enforcement and the scan resolved"
                        + " different Method objects for the same endpoint")
                .isNotEqualTo(403);
    }

    @Test
    @DisplayName("inherited-and-overridden method: refused without the permission")
    @WithMockUser(authorities = "component.rack:create")
    void overriddenMethodRefusesWithoutItsPermission() throws Exception {
        mockMvc.perform(get(RACKS + "/" + ABSENT).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("purely inherited method: the subclass's resource, not the base class's")
    @WithMockUser(authorities = "component.rack:delete")
    void inheritedMethodResolvesItsPermission() throws Exception {
        int status = mockMvc.perform(delete(RACKS + "/" + ABSENT)).andReturn().getResponse().getStatus();
        assertThat(status)
                .as("delete is declared once on AbstractCrudController for nine subclasses and is"
                        + " not overridden anywhere, so it arrives as the base class's own Method;"
                        + " a 403 means the concrete controller's resource was not applied")
                .isNotEqualTo(403);
    }

    @Test
    @DisplayName("purely inherited method: refused without the permission")
    @WithMockUser(authorities = "component.rack:read")
    void inheritedMethodRefusesWithoutItsPermission() throws Exception {
        mockMvc.perform(delete(RACKS + "/" + ABSENT)).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("controller with its own methods: the ordinary case still works")
    @WithMockUser(authorities = "catalog.component-type:read")
    void ownMethodResolvesItsPermission() throws Exception {
        mockMvc.perform(get(COMPONENT_TYPES).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("controller with its own methods: refused without the permission")
    @WithMockUser(authorities = "catalog.component-type:update")
    void ownMethodRefusesWithoutItsPermission() throws Exception {
        mockMvc.perform(get(COMPONENT_TYPES).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("an explicitly exempt route is not closed by enforcement")
    @WithMockUser(authorities = "nothing.at:all")
    void exemptRouteStaysOpen() throws Exception {
        int status = mockMvc.perform(get("/api/auth/check").accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getStatus();
        assertThat(status)
                .as("/api/auth/** answers who you are and what you may do, so it cannot itself"
                        + " require a permission. A 403 here would mean nobody can log in - the one"
                        + " failure that takes the whole frontend with it. Whether the call then"
                        + " succeeds is beside the point: this mock principal is not an AuthUser")
                .isNotEqualTo(403);
    }

    @Test
    @DisplayName("a catalog write is open to the permission holder, with no role at all")
    @WithMockUser(authorities = "catalog.component-type:delete")
    void catalogWriteIsOpenToThePermissionHolder() throws Exception {
        int status = mockMvc.perform(delete(COMPONENT_TYPES + "/" + ABSENT))
                .andReturn().getResponse().getStatus();
        assertThat(status)
                .as("this principal holds no ROLE_ADMIN, and until the catalog domain was promoted"
                        + " a hasRole('ADMIN') on this method would have refused it. That the"
                        + " permission alone now suffices is the whole point of the promotion:"
                        + " role_permissions decides, and it is a table rather than a rebuild")
                .isNotEqualTo(403);
    }

    @Test
    @DisplayName("holding every other permission is not holding this one")
    @WithMockUser(authorities = {"ROLE_ADMIN", "component.rack:read", "component.rack:create",
            "component.rack:update"})
    void rolesDoNotSubstituteForPermissions() throws Exception {
        mockMvc.perform(delete(RACKS + "/" + ABSENT)).andExpect(status().isForbidden());
    }
}
