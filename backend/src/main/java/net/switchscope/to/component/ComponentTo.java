package net.switchscope.to.component;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.security.policy.FieldAccess;
import net.switchscope.security.policy.FieldAccessLevel;
import net.switchscope.to.NamedTo;
import net.switchscope.to.component.connectivity.CableRunTo;
import net.switchscope.to.component.connectivity.ConnectorTo;
import net.switchscope.to.component.connectivity.PatchPanelTo;
import net.switchscope.to.component.device.AccessPointTo;
import net.switchscope.to.component.device.NetworkSwitchTo;
import net.switchscope.to.component.device.RouterTo;
import net.switchscope.to.component.housing.RackTo;
import net.switchscope.validation.NoHtml;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Base DTO for all component types.
 * <p>
 * Polymorphic over the {@code componentClass} property, whose values mirror the JPA
 * {@code @DiscriminatorValue} of the corresponding entity. {@code EXISTING_PROPERTY} is used so that
 * the discriminator is an ordinary field of the DTO: it is populated by the mappers on the way out
 * and consumed by Jackson on the way in. {@code defaultImpl} keeps requests that are deserialized
 * directly into a concrete subtype (e.g. {@code PUT /api/components/&#123;id&#125;}, which resolves the
 * type from the stored entity) working even when the payload omits the property.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "componentClass",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = NetworkSwitchTo.class, name = "NETWORK_SWITCH"),
        @JsonSubTypes.Type(value = RouterTo.class, name = "ROUTER"),
        @JsonSubTypes.Type(value = AccessPointTo.class, name = "ACCESS_POINT"),
        @JsonSubTypes.Type(value = CableRunTo.class, name = "CABLE_RUN"),
        @JsonSubTypes.Type(value = ConnectorTo.class, name = "CONNECTOR"),
        @JsonSubTypes.Type(value = PatchPanelTo.class, name = "PATCH_PANEL"),
        @JsonSubTypes.Type(value = RackTo.class, name = "RACK")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public abstract class ComponentTo extends NamedTo {

    @Schema(description = "Component discriminator (NETWORK_SWITCH, ROUTER, ACCESS_POINT, CABLE_RUN,"
            + " CONNECTOR, PATCH_PANEL, RACK). Required when posting to the polymorphic endpoints.")
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String componentClass;

    @Size(max = 128)
    @NoHtml
    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private String manufacturer;

    @Size(max = 128)
    @NoHtml
    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private String model;

    @Size(max = 128)
    @NoHtml
    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private String serialNumber;

    @Size(max = 128)
    @NoHtml
    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private String partNumber;

    @NotNull
    @Schema(description = "Component status ID")
    @FieldAccess(FieldAccessLevel.REQUIRED)
    private UUID componentStatusId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Component status code")
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String componentStatusCode;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Component status display name")
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String componentStatusDisplayName;

    @NotNull
    @Schema(description = "Component type ID")
    @FieldAccess(FieldAccessLevel.REQUIRED)
    private UUID componentTypeId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Component type code")
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String componentTypeCode;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Component type display name")
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String componentTypeDisplayName;

    @Schema(description = "Component nature ID (optional)")
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private UUID componentNatureId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Component nature code")
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String componentNatureCode;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private LocalDateTime lastMaintenanceDate;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private LocalDateTime nextMaintenanceDate;

    @Schema(description = "Installation ID (optional)")
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private UUID installationId;

    @Schema(description = "Parent component ID (optional)")
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private UUID parentComponentId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Parent component name")
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String parentComponentName;

    // Read-only computed fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String componentPath;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean operational;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean installed;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String locationAddress;

    protected ComponentTo(UUID id, OffsetDateTime createdAt, OffsetDateTime updatedAt, String name) {
        super(id, name);
        this.setCreatedAt(createdAt);
        this.setUpdatedAt(updatedAt);
    }

    protected ComponentTo(UUID id, String name) {
        super(id, name);
    }
}

