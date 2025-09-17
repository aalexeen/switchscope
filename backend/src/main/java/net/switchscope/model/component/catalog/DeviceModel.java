package net.switchscope.model.component.catalog;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.switchscope.model.NamedEntity;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.validation.NoHtml;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * Base abstract class for all device model catalog entries
 * Contains common fields and methods shared by all device models
 */
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class DeviceModel extends NamedEntity {

    // Basic identification (common for all devices)
    @Column(name = "manufacturer", nullable = false)
    @NotBlank
    @Size(max = 128)
    @NoHtml
    private String manufacturer;

    @Column(name = "model_number", nullable = false)
    @NotBlank
    @Size(max = 128)
    @NoHtml
    private String modelNumber;

    @Column(name = "product_family")
    @Size(max = 128)
    @NoHtml
    private String productFamily;

    @Column(name = "series")
    @Size(max = 128)
    @NoHtml
    private String series;

    // Reference to component type (common for all devices)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "component_type_id", nullable = false)
    @NotNull
    private ComponentTypeEntity componentType;

    // Physical specifications (common for all devices)
    @Column(name = "width_mm")
    private Integer widthMm;

    @Column(name = "depth_mm")
    private Integer depthMm;

    @Column(name = "height_mm")
    private Integer heightMm;

    @Column(name = "weight_kg")
    private BigDecimal weightKg;

    // Power specifications (common for all devices)
    @Column(name = "power_consumption_watts", nullable = false)
    @NotNull
    @Min(1)
    private Integer powerConsumptionWatts;

    @Column(name = "max_power_consumption_watts")
    private Integer maxPowerConsumptionWatts;

    @Column(name = "power_input_type")
    @Size(max = 64)
    private String powerInputType;

    @Column(name = "redundant_power_supply", nullable = false)
    private boolean redundantPowerSupply = false;

    // Management capabilities (common for all devices)
    @Column(name = "management_interfaces")
    @Size(max = 256)
    private String managementInterfaces;

    @Column(name = "snmp_versions")
    @Size(max = 64)
    private String snmpVersions = "v1,v2c";

    @Column(name = "supports_ssh", nullable = false)
    private boolean supportsSsh = true;

    @Column(name = "supports_telnet", nullable = false)
    private boolean supportsTelnet = true;

    @Column(name = "web_management", nullable = false)
    private boolean webManagement = true;

    // Environmental specifications (common for all devices)
    @Column(name = "operating_temp_min")
    private Integer operatingTempMin = 0;

    @Column(name = "operating_temp_max")
    private Integer operatingTempMax = 45;

    @Column(name = "humidity_min")
    private Integer humidityMin = 10;

    @Column(name = "humidity_max")
    private Integer humidityMax = 95;

    @Column(name = "fanless", nullable = false)
    private boolean fanless = false;

    @Column(name = "noise_level_db")
    private Integer noiseLevelDb;

    // Lifecycle information (common for all devices)
    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "end_of_sale_date")
    private LocalDate endOfSaleDate;

    @Column(name = "end_of_support_date")
    private LocalDate endOfSupportDate;

    @Column(name = "is_active_product", nullable = false)
    private boolean activeProduct = true;

    // Pricing and availability (common for all devices)
    @Column(name = "list_price")
    private BigDecimal listPrice;

    @Column(name = "currency", length = 3)
    @Size(min = 3, max = 3)
    private String currency = "USD";

    // Additional specifications as key-value pairs (common for all devices)
    @ElementCollection
    @CollectionTable(name = "device_model_specifications",
                    joinColumns = @JoinColumn(name = "device_model_id"))
    @MapKeyColumn(name = "spec_name")
    @Column(name = "spec_value")
    private Map<String, String> additionalSpecs = new HashMap<>();

    // Documentation and resources (common for all devices)
    @ElementCollection
    @CollectionTable(name = "device_model_resources",
                    joinColumns = @JoinColumn(name = "device_model_id"))
    @Column(name = "resource_url")
    private Set<String> resourceUrls = new HashSet<>();

    // Protected constructor for subclasses
    protected DeviceModel(String name, String manufacturer, String modelNumber,
                         ComponentTypeEntity componentType, Integer powerConsumptionWatts) {
        super(null, name);
        this.manufacturer = manufacturer;
        this.modelNumber = modelNumber;
        this.componentType = componentType;
        this.powerConsumptionWatts = powerConsumptionWatts;
    }

    // Common business methods
    /**
     * Check if this is a current/active product
     */
    public boolean isCurrentProduct() {
        return activeProduct && (endOfSaleDate == null ||
               endOfSaleDate.isAfter(LocalDate.now()));
    }

    /**
     * Get full model identifier (manufacturer + model number)
     */
    public String getFullModelId() {
        return manufacturer + " " + modelNumber;
    }

    /**
     * Check if device has environmental specifications
     */
    public boolean hasEnvironmentalSpecs() {
        return operatingTempMin != null || operatingTempMax != null ||
               humidityMin != null || humidityMax != null;
    }

    /**
     * Get environmental operating range summary
     */
    public String getEnvironmentalSummary() {
        StringBuilder env = new StringBuilder();

        if (operatingTempMin != null && operatingTempMax != null) {
            env.append("Temp: ").append(operatingTempMin).append("°C to ")
               .append(operatingTempMax).append("°C");
        }

        if (humidityMin != null && humidityMax != null) {
            if (env.length() > 0) env.append(", ");
            env.append("Humidity: ").append(humidityMin).append("% to ")
               .append(humidityMax).append("%");
        }

        if (fanless) {
            if (env.length() > 0) env.append(", ");
            env.append("Fanless");
        }

        return env.toString();
    }

    /**
     * Calculate power efficiency (device-specific implementation needed)
     */
    public abstract BigDecimal getPowerEfficiency();

    /**
     * Get device type specific summary
     */
    public abstract String getDeviceTypeSummary();

    // Common specification management methods
    /**
     * Add custom specification
     */
    public void addSpecification(String name, String value) {
        if (name != null && !name.trim().isEmpty() && value != null) {
            additionalSpecs.put(name, value);
        }
    }

    /**
     * Get custom specification
     */
    public String getSpecification(String name) {
        return additionalSpecs.get(name);
    }

    /**
     * Check if has custom specification
     */
    public boolean hasSpecification(String name) {
        return additionalSpecs.containsKey(name);
    }

    /**
     * Add resource URL (datasheet, manual, etc.)
     */
    public void addResourceUrl(String url) {
        if (url != null && !url.trim().isEmpty()) {
            resourceUrls.add(url);
        }
    }

    /**
     * Remove resource URL
     */
    public void removeResourceUrl(String url) {
        resourceUrls.remove(url);
    }

    /**
     * Check if has resource URLs
     */
    public boolean hasResourceUrls() {
        return !resourceUrls.isEmpty();
    }

    /**
     * Basic validation common to all device models
     */
    public boolean isValidModel() {
        if (manufacturer == null || manufacturer.trim().isEmpty() ||
            modelNumber == null || modelNumber.trim().isEmpty() ||
            componentType == null ||
            powerConsumptionWatts == null || powerConsumptionWatts <= 0) {
            return false;
        }

        // Validate environmental ranges
        if (operatingTempMin != null && operatingTempMax != null &&
            operatingTempMin >= operatingTempMax) {
            return false;
        }

        if (humidityMin != null && humidityMax != null &&
            humidityMin >= humidityMax) {
            return false;
        }

        // Validate power consumption
        if (maxPowerConsumptionWatts != null &&
            maxPowerConsumptionWatts < powerConsumptionWatts) {
            return false;
        }

        return true;
    }

    /**
     * Get basic device info summary
     */
    public String getBasicSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append(manufacturer).append(" ").append(modelNumber);

        if (productFamily != null) {
            summary.append(" (").append(productFamily).append(")");
        }

        summary.append(" - ").append(powerConsumptionWatts).append("W");

        if (!isCurrentProduct()) {
            summary.append(" [EOL]");
        }

        return summary.toString();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + manufacturer + " " + modelNumber +
               ", " + powerConsumptionWatts + "W]";
    }
}