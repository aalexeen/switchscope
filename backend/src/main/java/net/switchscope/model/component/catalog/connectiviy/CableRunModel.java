package net.switchscope.model.component.catalog.connectiviy;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.model.component.catalog.ComponentModel;
import net.switchscope.validation.NoHtml;

import java.math.BigDecimal;

/**
 * Cable Run Model Catalog Entry - represents a specific cable type and specifications
 */
@Entity
@Table(name = "cable_run_models",
       uniqueConstraints = @UniqueConstraint(columnNames = {"manufacturer", "model_number"}))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CableRunModel extends ComponentModel {

    // Cable type and category
    @Column(name = "cable_type", nullable = false)
    @NotBlank
    @Size(max = 64)
    @NoHtml
    private String cableType; // CAT5E, CAT6, CAT6A, FIBER_MULTIMODE, FIBER_SINGLEMODE, COAX

    @Column(name = "cable_category")
    @Size(max = 32)
    @NoHtml
    private String cableCategory; // Category 5e, Category 6, Category 6A, etc.

    // Physical specifications
    @Column(name = "conductor_gauge")
    @Size(max = 16)
    @NoHtml
    private String conductorGauge; // 24 AWG, 23 AWG, etc.

    @Column(name = "conductor_count")
    @Min(1) @Max(1000)
    private Integer conductorCount; // 4 pairs for UTP, fiber count for optical

    @Column(name = "jacket_type")
    @Size(max = 64)
    @NoHtml
    private String jacketType; // PVC, LSZH, Plenum, Riser

    @Column(name = "shielding_type")
    @Size(max = 32)
    @NoHtml
    private String shieldingType; // UTP, STP, FTP, SFTP

    // Performance specifications
    @Column(name = "max_frequency_mhz")
    private Integer maxFrequencyMhz;

    @Column(name = "max_bandwidth_gbps")
    private BigDecimal maxBandwidthGbps;

    @Column(name = "max_distance_meters")
    @Min(1)
    private Integer maxDistanceMeters;

    @Column(name = "attenuation_db_per_100m")
    private BigDecimal attenuationDbPer100m;

    // Environmental specifications
    @Column(name = "operating_temperature_min")
    private Integer operatingTemperatureMin;

    @Column(name = "operating_temperature_max")
    private Integer operatingTemperatureMax;

    @Column(name = "indoor_use", nullable = false)
    private boolean indoorUse = true;

    @Column(name = "outdoor_use", nullable = false)
    private boolean outdoorUse = false;

    @Column(name = "plenum_rated", nullable = false)
    private boolean plenumRated = false;

    @Column(name = "fire_retardant", nullable = false)
    private boolean fireRetardant = false;

    // Installation specifications
    @Column(name = "bend_radius_mm")
    @Min(1)
    private Integer bendRadiusMm;

    @Column(name = "pulling_tension_lbs")
    @Min(1)
    private Integer pullingTensionLbs;

    // Constructor
    public CableRunModel(String name, String manufacturer, String modelNumber,
                        ComponentTypeEntity componentType, String cableType) {
        super(name, manufacturer, modelNumber, componentType);
        this.cableType = cableType;
    }

    // Business methods
    public boolean isFiberOptic() {
        return cableType != null && cableType.toLowerCase().contains("fiber");
    }

    public boolean isCopper() {
        return cableType != null && (cableType.startsWith("CAT") || "COAX".equals(cableType));
    }

    public boolean isPlenumSafe() {
        return plenumRated && fireRetardant;
    }

    public String getPerformanceSummary() {
        StringBuilder summary = new StringBuilder();
        if (maxBandwidthGbps != null) {
            summary.append(maxBandwidthGbps).append(" Gbps");
        }
        if (maxFrequencyMhz != null) {
            if (summary.length() > 0) summary.append(", ");
            summary.append(maxFrequencyMhz).append(" MHz");
        }
        if (maxDistanceMeters != null) {
            if (summary.length() > 0) summary.append(", ");
            summary.append(maxDistanceMeters).append("m max");
        }
        return summary.toString();
    }

    @Override
    public boolean isValidModel() {
        if (!super.isValidModel()) {
            return false;
        }

        if (cableType == null || cableType.trim().isEmpty()) {
            return false;
        }

        // Fiber cables should have conductor count
        if (isFiberOptic() && (conductorCount == null || conductorCount <= 0)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return getManufacturer() + " " + getModelNumber() +
               " (" + cableType +
               (getPerformanceSummary().isEmpty() ? "" : ", " + getPerformanceSummary()) + ")";
    }
}