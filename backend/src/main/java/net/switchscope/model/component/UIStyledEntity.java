package net.switchscope.model.component;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Base class for entities with UI styling properties
 */
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class UIStyledEntity extends BaseCodedEntity {

    @Column(name = "color_class")
    @Size(max = 128)
    private String colorClass; // CSS class for UI colors

    @Column(name = "icon_class")
    @Size(max = 128)
    private String iconClass; // CSS class for UI icons

    protected UIStyledEntity(String code, String displayName) {
        super(code, displayName);
    }

    protected UIStyledEntity(UUID id, String code, String displayName, String description) {
        super(id, code, displayName, description);
    }

    protected UIStyledEntity(String code, String displayName, String colorClass) {
        super(code, displayName);
        this.colorClass = colorClass;
    }
}