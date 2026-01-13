package net.switchscope.to.component.catalog;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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
    private String lifecyclePhase;

    // Boolean flags for business logic
    private Boolean available = false;

    private Boolean operational = false;

    private Boolean canAcceptInstallations = false;

    private Boolean requiresAttention = false;

    private Boolean physicallyPresent = false;

    private Boolean inInventory = true;

    private Boolean inTransition = false;

    private Boolean canBeReserved = false;

    // Custom properties for extensibility
    private Map<String, String> properties = new HashMap<>();

    // Read-only: next possible status codes
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Set<String> nextPossibleStatusCodes = new HashSet<>();

    public ComponentStatusTo(UUID id, String name, String code, String displayName) {
        super(id, name, code, displayName);
    }

    public ComponentStatusTo(UUID id, String name) {
        super(id, name);
    }
}

