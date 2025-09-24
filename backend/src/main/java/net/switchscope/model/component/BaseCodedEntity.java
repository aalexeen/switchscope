package net.switchscope.model.component;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.switchscope.model.NamedEntity;
import net.switchscope.validation.NoHtml;

import java.util.UUID;

/**
 * Base class for coded entities with common attributes
 */
@MappedSuperclass
@Table(indexes = @Index(name = "idx_base_active_sort", columnList = "is_active,sort_order"),
uniqueConstraints = {@UniqueConstraint(columnNames = {"code"}, name = "uk_code")})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseCodedEntity extends NamedEntity {

    @Column(name = "code", nullable = false)
    @Size(max = 64)
    @NotNull
    @NoHtml
    private String code; // Unique identifier

    @Column(name = "display_name", nullable = false)
    @Size(max = 128)
    @NotNull
    @NoHtml
    private String displayName;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    protected BaseCodedEntity(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
        this.name = displayName; // NamedEntity requirement
    }

    protected BaseCodedEntity(String code, String name, String displayName) {
        super(null, name, null); // NamedEntity(UUID, name, description)
        this.code = code;
        this.displayName = displayName;
    }


    protected BaseCodedEntity(UUID id, String code, String displayName, String description) {
        super(id, displayName, description);
        this.code = code;
        this.displayName = displayName;
    }

    protected BaseCodedEntity(String code, String name, String displayName, String description) {
        super(null, name, description); // NamedEntity(UUID, name, description)
        this.code = code;
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + code + ":" + displayName + "]";
    }
}