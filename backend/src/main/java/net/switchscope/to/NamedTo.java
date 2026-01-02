package net.switchscope.to;

import net.switchscope.validation.NoHtml;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class NamedTo extends BaseTo {
    @NotBlank
    @Size(min = 2, max = 128)
    @NoHtml
    protected String name;

    public NamedTo(UUID id, String name) {
        super(id);
        this.name = name;
    }

    @Override
    public String toString() {
        return super.toString() + '[' + name + ']';
    }
}

