package net.switchscope.to.component.catalog;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.security.policy.FieldAccess;
import net.switchscope.security.policy.FieldAccessLevel;
import net.switchscope.validation.NoHtml;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for ComponentStatusEntity - dynamic component status catalog
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ComponentStatusTo extends UIStyledTo {

    @Size(max = 64)
    @NoHtml
    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private String lifecyclePhase;

    // Boolean flags for business logic
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean available = false;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean operational = false;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean canAcceptInstallations = false;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean requiresAttention = false;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean physicallyPresent = false;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean inInventory = true;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean inTransition = false;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean canBeReserved = false;

    // Custom properties for extensibility
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Map<String, String> properties = new HashMap<>();

    // Read-only: next possible status codes
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Set<String> nextPossibleStatusCodes = new HashSet<>();

    public ComponentStatusTo(UUID id, String name, String code, String displayName) {
        super(id, name, code, displayName);
    }

    public ComponentStatusTo(UUID id, String name) {
        super(id, name);
    }
}

