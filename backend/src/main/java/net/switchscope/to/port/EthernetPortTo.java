package net.switchscope.to.port;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.validation.NoHtml;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO for EthernetPort entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class EthernetPortTo extends PortTo {

    // Ethernet-specific fields
    @Size(max = 64)
    @NoHtml
    private String ethernetStandard;

    @Size(max = 16)
    @NoHtml
    private String mdiMdixMode;

    private Integer cableLengthMeters;

    @Size(max = 256)
    @NoHtml
    private String linkPartnerInfo;

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean gigabitCapable;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean tenGigabitCapable;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String ethernetClass;

    public EthernetPortTo(UUID id, OffsetDateTime createdAt, OffsetDateTime updatedAt, String name) {
        super(id, createdAt, updatedAt, name);
    }

    public EthernetPortTo(UUID id, String name) {
        super(id, name);
    }
}

