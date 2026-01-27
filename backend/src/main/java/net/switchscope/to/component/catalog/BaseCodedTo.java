package net.switchscope.to.component.catalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.security.policy.FieldAccess;
import net.switchscope.security.policy.FieldAccessLevel;
import net.switchscope.to.NamedTo;
import net.switchscope.validation.NoHtml;

import java.util.UUID;

/**
 * Base DTO for coded catalog entities
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public abstract class BaseCodedTo extends NamedTo {

    @NotBlank
    @Size(max = 64)
    @NoHtml
    @FieldAccess(FieldAccessLevel.REQUIRED)
    private String code;

    @NotBlank
    @Size(max = 128)
    @NoHtml
    @FieldAccess(FieldAccessLevel.REQUIRED)
    private String displayName;

    @NotNull
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean active = true;

    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private Integer sortOrder = 0;

    protected BaseCodedTo(UUID id, String name, String code, String displayName) {
        super(id, name);
        this.code = code;
        this.displayName = displayName;
    }

    protected BaseCodedTo(UUID id, String name) {
        super(id, name);
    }
}

