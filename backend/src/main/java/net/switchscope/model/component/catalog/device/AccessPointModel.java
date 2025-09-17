package net.switchscope.model.component.catalog.device;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.validation.NoHtml;

import java.math.BigDecimal;
import java.util.*;

/**
 * Access Point Model Catalog Entry - represents a specific wireless access point model
 */
@Entity
@Table(name = "access_point_models",
       uniqueConstraints = @UniqueConstraint(columnNames = {"manufacturer", "model_number"}))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccessPointModel extends DeviceModel {

    // Access Point-specific fields only (common fields moved to DeviceModel)

    // WiFi specifications
    @Column(name = "wifi_standard", nullable = false)
    @NotBlank
    @Size(max = 32)
    @NoHtml
    private String wifiStandard; // 802.11ac, 802.11ax (WiFi 6), 802.11be (WiFi 7)

    @Column(name = "wifi_generation")
    @Size(max = 32)
    @NoHtml
    private String wifiGeneration; // WiFi 5, WiFi 6, WiFi 6E, WiFi 7

    @Column(name = "max_clients", nullable = false)
    @NotNull
    @Min(1) @Max(1000)
    private Integer maxClients;

    // Band support
    @Column(name = "dual_band", nullable = false)
    private boolean dualBand = false;

    @Column(name = "supports_5ghz", nullable = false)
    private boolean supports5Ghz = false;

    @Column(name = "supports_6ghz", nullable = false)
    private boolean supports6Ghz = false;

    // Performance specifications
    @Column(name = "max_throughput_mbps", nullable = false)
    @NotNull
    @Min(1) @Max(10000)
    private Integer maxThroughputMbps;

    // Coverage specifications
    @Column(name = "indoor_coverage_sqm")
    private Integer indoorCoverageSqm;

    @Column(name = "outdoor_coverage_sqm")
    private Integer outdoorCoverageSqm;

    // Physical specifications (AP-specific)
    @Column(name = "form_factor")
    @Size(max = 64)
    @NoHtml
    private String formFactor; // CEILING_MOUNT, WALL_MOUNT, DESKTOP

    @Column(name = "weight_g")
    private Integer weightG;

    @Column(name = "ip_rating")
    @Size(max = 8)
    @NoHtml
    private String ipRating; // IP67, IP54, etc.

    // PoE specifications
    @Column(name = "poe_required", nullable = false)
    private boolean poeRequired = true;

    @Column(name = "poe_standard")
    @Size(max = 32)
    @NoHtml
    private String poeStandard; // 802.3at (PoE+), 802.3bt (PoE++)

    // Security features
    @Column(name = "supports_wpa3", nullable = false)
    private boolean supportsWpa3 = false;

    @Column(name = "supports_enterprise_auth", nullable = false)
    private boolean supportsEnterpriseAuth = false;

    // Advanced features
    @Column(name = "supports_mesh", nullable = false)
    private boolean supportsMesh = false;

    @Column(name = "supports_mu_mimo", nullable = false)
    private boolean supportsMuMimo = false;

    @Column(name = "supports_beamforming", nullable = false)
    private boolean supportsBeamforming = false;

    // Management capabilities
    @Column(name = "controller_managed", nullable = false)
    private boolean controllerManaged = false;

    @Column(name = "cloud_managed", nullable = false)
    private boolean cloudManaged = false;

    // Environment
    @Column(name = "indoor_use", nullable = false)
    private boolean indoorUse = true;

    @Column(name = "outdoor_use", nullable = false)
    private boolean outdoorUse = false;

    // Constructor
    public AccessPointModel(String name, String manufacturer, String modelNumber,
                           ComponentTypeEntity componentType, String wifiStandard,
                           Integer maxClients, Integer powerConsumptionWatts) {
        super(name, manufacturer, modelNumber, componentType, powerConsumptionWatts);
        this.wifiStandard = wifiStandard;
        this.maxClients = maxClients;
    }

    // Implementation of abstract methods from DeviceModel
    @Override
    public BigDecimal getPowerEfficiency() {
        if (getPowerConsumptionWatts() == null || maxThroughputMbps == null || maxThroughputMbps == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(maxThroughputMbps)
                         .divide(BigDecimal.valueOf(getPowerConsumptionWatts()), 2, java.math.RoundingMode.HALF_UP);
    }

    @Override
    public String getDeviceTypeSummary() {
        return wifiStandard + ", " + maxClients + " clients" +
               (supportsWpa3 ? ", WPA3" : "") +
               (supportsMesh ? ", Mesh" : "");
    }

    // Access Point-specific business methods
    public boolean isWiFi6() {
        return "802.11ax".equals(wifiStandard) ||
               (wifiGeneration != null && wifiGeneration.contains("WiFi 6"));
    }

    public boolean isEnterprise() {
        return supportsEnterpriseAuth || controllerManaged;
    }

    public boolean hasAdvancedFeatures() {
        return supportsMuMimo || supportsBeamforming;
    }

    public String getBandSummary() {
        List<String> bands = new ArrayList<>();
        bands.add("2.4GHz"); // All APs support 2.4GHz
        if (supports5Ghz) bands.add("5GHz");
        if (supports6Ghz) bands.add("6GHz");
        return String.join(" + ", bands);
    }

    // Enhanced validation specific to access points
    @Override
    public boolean isValidModel() {
        if (!super.isValidModel()) {
            return false;
        }

        if (wifiStandard == null || wifiStandard.trim().isEmpty() ||
            maxClients == null || maxClients <= 0 ||
            maxThroughputMbps == null || maxThroughputMbps <= 0) {
            return false;
        }

        // PoE validation
        if (poeRequired && (poeStandard == null || poeStandard.trim().isEmpty())) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return getManufacturer() + " " + getModelNumber() +
               " (" + wifiStandard + ", " + maxClients + " clients, " +
               maxThroughputMbps + " Mbps, " + getPowerConsumptionWatts() + "W)";
    }
}