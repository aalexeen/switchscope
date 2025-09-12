package net.switchscope.to;

import io.swagger.v3.oas.annotations.media.Schema;
import net.switchscope.HasId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Data
public abstract class BaseTo implements HasId {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY) // https://stackoverflow.com/a/28025008/548473
    protected UUID id;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    protected OffsetDateTime createdAt;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    protected OffsetDateTime updatedAt;


    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + id;
    }
}
