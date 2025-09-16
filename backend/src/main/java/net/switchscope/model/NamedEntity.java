package net.switchscope.model;

import net.switchscope.validation.NoHtml;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class NamedEntity extends BaseEntity {

    @NotBlank
    @Size(min = 2, max = 128)
    @Column(name = "name", nullable = false)
    @NoHtml
    protected String name;

    @Size(max = 512)
    @Column(name = "description")
    @NoHtml
    private String description;

    protected NamedEntity(UUID id, String name, String description) {
        super(id);
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return super.toString() + '[' + name + ']';
    }
}
