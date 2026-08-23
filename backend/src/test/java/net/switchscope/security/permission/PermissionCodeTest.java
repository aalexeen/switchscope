package net.switchscope.security.permission;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PermissionCodeTest {

    @Test
    @DisplayName("an action is qualified by the controller's resource")
    void joinsResourceAndAction() {
        assertThat(PermissionCode.join("catalog.component-type", "update"))
                .isEqualTo("catalog.component-type:update");
    }

    @Test
    @DisplayName("a value that already carries an action is taken verbatim")
    void passesFullCodeThrough() {
        assertThat(PermissionCode.join("location", "system.permission:read"))
                .isEqualTo("system.permission:read");
    }

    @Test
    @DisplayName("a bare action with no resource is a configuration error, not a silent default")
    void refusesBareActionWithoutResource() {
        assertThatThrownBy(() -> PermissionCode.join(null, "update"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("@PermissionResource");
    }

    @Test
    @DisplayName("the domain is the segment before the first dot")
    void parsesDottedResource() {
        PermissionCode parsed = PermissionCode.parse("catalog.component-type:update");
        assertThat(parsed.domain()).isEqualTo("catalog");
        assertThat(parsed.resource()).isEqualTo("catalog.component-type");
        assertThat(parsed.action()).isEqualTo("update");
    }

    @Test
    @DisplayName("a resource with no dot is its own domain")
    void parsesFlatResource() {
        assertThat(PermissionCode.parse("location:delete"))
                .isEqualTo(new PermissionCode("location", "location", "delete"));
    }

    @Test
    @DisplayName("a code with no action does not parse")
    void rejectsCodeWithoutAction() {
        assertThatThrownBy(() -> PermissionCode.parse("location"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
