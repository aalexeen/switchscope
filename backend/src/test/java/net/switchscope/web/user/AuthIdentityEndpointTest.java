package net.switchscope.web.user;

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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * What {@code /api/auth/login} and {@code /api/auth/check} tell the client about itself, now that
 * stage 3 has the frontend gating routes and buttons on it.
 *
 * <h2>Why "permissions is not empty" would prove nothing</h2>
 * Both seeded roles hold nearly everything - USER has 80 of the 81 permissions - so a list that is
 * merely non-empty is equally consistent with a real answer and with a hardcoded one. The
 * assertion that discriminates is the single permission the two roles differ on:
 * {@code system.permission:read}. It has to be present for ADMIN, absent for USER, and the
 * endpoint it guards has to answer accordingly. That is the same "delete one grant and watch the
 * answer change" check that established enforcement was real, expressed as the difference between
 * two roles instead of two runs.
 *
 * <h2>Why roles are asserted at all</h2>
 * They still serialise as {@code ["ADMIN"]}, and {@code services/auth.js} does
 * {@code roles.includes('ADMIN')} on exactly that shape. Stage 4 replaces the enum behind them;
 * this pins the wire shape so that replacement cannot quietly change it.
 */
@SpringBootTest(properties = {
        "spring.testcontainers.enabled=false",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration"
})
@AutoConfigureMockMvc
class AuthIdentityEndpointTest extends AbstractContextTest {

    private static final String LOGIN = "/api/auth/login";
    private static final String CHECK = "/api/auth/check";
    private static final String MATRIX = "/api/admin/permissions/matrix";

    private static final String USER = "user@gmail.com";
    private static final String USER_PASSWORD = "password";
    private static final String ADMIN = "admin@gmail.com";
    private static final String ADMIN_PASSWORD = "admin";

    /** The one permission the two seeded roles differ on. */
    private static final String ADMIN_ONLY = "system.permission:read";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("login answers with the permissions the caller holds, and the roles unchanged")
    void loginCarriesPermissionsAndRoles() throws Exception {
        JsonNode body = identity(post(LOGIN).contentType(MediaType.APPLICATION_JSON).content("{}"),
                USER, USER_PASSWORD);

        assertThat(strings(body.get("roles")))
                .as("the frontend does roles.includes('USER') on this very shape")
                .containsExactly("USER");
        assertThat(strings(body.get("permissions")))
                .contains("catalog.component-type:read", "component.rack:delete", "location:update");
    }

    @Test
    @DisplayName("check answers the same identity as login")
    void checkAnswersTheSameIdentity() throws Exception {
        JsonNode fromLogin = identity(post(LOGIN).contentType(MediaType.APPLICATION_JSON).content("{}"),
                USER, USER_PASSWORD);
        JsonNode fromCheck = identity(get(CHECK), USER, USER_PASSWORD);

        assertThat(fromCheck)
                .as("the two differ in when the client asks, not in what it gets back")
                .isEqualTo(fromLogin);
    }

    @Test
    @DisplayName("the list is the caller's own: the permission USER lacks is missing, and refused")
    void permissionsAreTheCallersOwn() throws Exception {
        assertThat(strings(identity(get(CHECK), ADMIN, ADMIN_PASSWORD).get("permissions")))
                .contains(ADMIN_ONLY);
        assertThat(strings(identity(get(CHECK), USER, USER_PASSWORD).get("permissions")))
                .doesNotContain(ADMIN_ONLY);

        mockMvc.perform(get(MATRIX).with(httpBasic(ADMIN, ADMIN_PASSWORD)))
                .andExpect(status().isOk());
        mockMvc.perform(get(MATRIX).with(httpBasic(USER, USER_PASSWORD)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("roles do not leak into permissions")
    void rolesAreNotPermissions() throws Exception {
        assertThat(strings(identity(get(CHECK), ADMIN, ADMIN_PASSWORD).get("permissions")))
                .as("both kinds share one authority collection; only the permission half belongs here")
                .noneMatch(code -> code.startsWith("ROLE_"));
    }

    private JsonNode identity(org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder request,
                              String email, String password) throws Exception {
        String json = mockMvc.perform(request.with(httpBasic(email, password)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(json);
    }

    private static List<String> strings(JsonNode array) {
        assertThat(array).as("field is present in the response").isNotNull();
        List<String> values = new ArrayList<>();
        array.forEach(node -> values.add(node.asText()));
        return values;
    }
}
