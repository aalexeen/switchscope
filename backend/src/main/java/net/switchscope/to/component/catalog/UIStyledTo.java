package net.switchscope.to.component.catalog;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.security.policy.FieldAccess;
import net.switchscope.security.policy.FieldAccessLevel;

import java.util.UUID;

/**
 * Base DTO for UI styled catalog entities with color and icon classes
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public abstract class UIStyledTo extends BaseCodedTo {

    @Size(max = 128)
    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private String colorClass;

    @Size(max = 128)
    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private String iconClass;

    protected UIStyledTo(UUID id, String name, String code, String displayName) {
        super(id, name, code, displayName);
    }

    protected UIStyledTo(UUID id, String name) {
        super(id, name);
    }
}

