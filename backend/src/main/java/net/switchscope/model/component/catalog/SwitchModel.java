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
import java.util.*;

/**
 * Switch Model Catalog Entry - represents a specific switch model from a manufacturer
 * This is a template/catalog entry, not an actual installed switch instance
 *
 * Examples:
 * - Cisco Catalyst 2960-X Series 24-Port Gigabit Switch
 * - HPE Aruba 2530 48G Switch
 * - Juniper EX2300-48P Switch
 */
@Entity
@Table(name = "switch_models",
       uniqueConstraints = @UniqueConstraint(columnNames = {"manufacturer", "model_number"}))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SwitchModel extends NamedEntity {

    // Basic identification
    @Column(name = "manufacturer", nullable = false)
    @NotBlank
    @Size(max = 128)
    @NoHtml
    private String manufacturer; // Cisco, HPE, Juniper, etc.

    @Column(name = "model_number", nullable = false)
    @NotBlank
    @Size(max = 128)
    @NoHtml
    private String modelNumber; // C2960X-24TS-L, 2530-48G, EX2300-48P

    @Column(name = "product_family")
    @Size(max = 128)
    @NoHtml
    private String productFamily; // Catalyst 2960-X, Aruba 2530, EX2300

    @Column(name = "series")
    @Size(max = 128)
    @NoHtml
    private String series; // 2960-X, 2530, EX2300

    // Reference to component type
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "component_type_id", nullable = false)
    @NotNull
    private ComponentTypeEntity componentType; // Should be SWITCH type

    // Switch classification
    @Enumerated(EnumType.STRING)
    @Column(name = "switch_class", nullable = false)
    @NotNull
    private SwitchClass switchClass = SwitchClass.MANAGED;

    @Enumerated(EnumType.STRING)
    @Column(name = "switch_tier")
    private SwitchTier switchTier = SwitchTier.ACCESS;

    @Column(name = "is_stackable", nullable = false)
    private boolean stackable = false;

    @Column(name = "max_stack_units")
    private Integer maxStackUnits;

    // Physical specifications
    @Column(name = "rack_units", nullable = false)
    @NotNull
    @Min(1) @Max(10)
    private Integer rackUnits = 1;

    @Column(name = "form_factor")
    @Enumerated(EnumType.STRING)
    private FormFactor formFactor = FormFactor.RACK_MOUNT;

    @Column(name = "width_mm")
    private Integer widthMm;

    @Column(name = "depth_mm")
    private Integer depthMm;

    @Column(name = "height_mm")
    private Integer heightMm;

    @Column(name = "weight_kg")
    private BigDecimal weightKg;

    // Port specifications
    @Column(name = "total_ports", nullable = false)
    @NotNull
    @Min(1) @Max(128)
    private Integer totalPorts;

    @Column(name = "gigabit_ports")
    @Min(0)
    private Integer gigabitPorts = 0;

    @Column(name = "fast_ethernet_ports")
    @Min(0)
    private Integer fastEthernetPorts = 0;

    @Column(name = "ten_gig_ports")
    @Min(0)
    private Integer tenGigPorts = 0;

    @Column(name = "sfp_ports")
    @Min(0)
    private Integer sfpPorts = 0;

    @Column(name = "sfp_plus_ports")
    @Min(0)
    private Integer sfpPlusPorts = 0;

    @Column(name = "console_ports")
    @Min(0)
    private Integer consolePorts = 1;

    // Power specifications
    @Column(name = "power_consumption_watts", nullable = false)
    @NotNull
    @Min(1) @Max(1000)
    private Integer powerConsumptionWatts;

    @Column(name = "max_power_consumption_watts")
    private Integer maxPowerConsumptionWatts;

    @Column(name = "supports_poe", nullable = false)
    private boolean supportsPoe = false;

    @Column(name = "poe_budget_watts")
    private Integer poeBudgetWatts;

    @Enumerated(EnumType.STRING)
    @Column(name = "poe_standard")
    private PoeStandard poeStandard;

    @Column(name = "power_input_type")
    @Size(max = 64)
    private String powerInputType; // AC, DC, External Adapter

    @Column(name = "redundant_power_supply", nullable = false)
    private boolean redundantPowerSupply = false;

    // Performance specifications
    @Column(name = "switching_capacity_gbps")
    private BigDecimal switchingCapacityGbps;

    @Column(name = "forwarding_rate_mpps")
    private BigDecimal forwardingRateMpps;

    @Column(name = "mac_address_table_size")
    private Integer macAddressTableSize;

    @Column(name = "buffer_size_mb")
    private BigDecimal bufferSizeMb;

    @Column(name = "latency_microseconds")
    private BigDecimal latencyMicroseconds;

    // Network features
    @Column(name = "max_vlans")
    private Integer maxVlans;

    @Column(name = "supports_spanning_tree", nullable = false)
    private boolean supportsSpanningTree = true;

    @Column(name = "supports_link_aggregation", nullable = false)
    private boolean supportsLinkAggregation = false;

    @Column(name = "supports_qos", nullable = false)
    private boolean supportsQos = false;

    @Column(name = "supports_igmp_snooping", nullable = false)
    private boolean supportsIgmpSnooping = false;

    @Column(name = "supports_port_mirroring", nullable = false)
    private boolean supportsPortMirroring = false;

    @Column(name = "supports_voice_vlan", nullable = false)
    private boolean supportsVoiceVlan = false;

    @Column(name = "supports_acl", nullable = false)
    private boolean supportsAcl = false;

    @Column(name = "layer3_routing", nullable = false)
    private boolean layer3Routing = false;

    // Management capabilities
    @Column(name = "management_interfaces")
    @Size(max = 256)
    private String managementInterfaces; // "Web GUI, CLI, SNMP v1/v2c/v3"

    @Column(name = "snmp_versions")
    @Size(max = 64)
    private String snmpVersions = "v1,v2c"; // Comma-separated

    @Column(name = "supports_ssh", nullable = false)
    private boolean supportsSsh = true;

    @Column(name = "supports_telnet", nullable = false)
    private boolean supportsTelnet = true;

    @Column(name = "web_management", nullable = false)
    private boolean webManagement = true;

    // Environmental specifications
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

    // Lifecycle information
    @Column(name = "release_date")
    private java.time.LocalDate releaseDate;

    @Column(name = "end_of_sale_date")
    private java.time.LocalDate endOfSaleDate;

    @Column(name = "end_of_support_date")
    private java.time.LocalDate endOfSupportDate;

    @Column(name = "is_active_product", nullable = false)
    private boolean activeProduct = true;

    // Pricing and availability
    @Column(name = "list_price")
    private BigDecimal listPrice;

    @Column(name = "currency", length = 3)
    @Size(min = 3, max = 3)
    private String currency = "USD";

    // Additional specifications as key-value pairs
    @ElementCollection
    @CollectionTable(name = "switch_model_specifications",
                    joinColumns = @JoinColumn(name = "switch_model_id"))
    @MapKeyColumn(name = "spec_name")
    @Column(name = "spec_value")
    private Map<String, String> additionalSpecs = new HashMap<>();

    // Documentation and resources
    @ElementCollection
    @CollectionTable(name = "switch_model_resources",
                    joinColumns = @JoinColumn(name = "switch_model_id"))
    @Column(name = "resource_url")
    private Set<String> resourceUrls = new HashSet<>(); // Datasheet URLs, manuals, etc.

    // Constructors
    public SwitchModel(String name, String manufacturer, String modelNumber,
                      ComponentTypeEntity componentType, Integer totalPorts,
                      Integer powerConsumptionWatts) {
        super(null, name);
        this.manufacturer = manufacturer;
        this.modelNumber = modelNumber;
        this.componentType = componentType;
        this.totalPorts = totalPorts;
        this.powerConsumptionWatts = powerConsumptionWatts;
    }

    // Business methods
    public boolean isManaged() {
        return switchClass != SwitchClass.UNMANAGED;
    }

    public boolean isEnterprise() {
        return switchTier == SwitchTier.CORE || switchTier == SwitchTier.DISTRIBUTION;
    }

    public boolean isCurrentProduct() {
        return activeProduct && (endOfSaleDate == null ||
               endOfSaleDate.isAfter(java.time.LocalDate.now()));
    }

    public boolean hasPoePlus() {
        return supportsPoe &&
               (poeStandard == PoeStandard.POE_PLUS || poeStandard == PoeStandard.POE_PLUS_PLUS);
    }

    public int getAvailablePowerBudget() {
        return poeBudgetWatts != null ? poeBudgetWatts : 0;
    }

    public boolean supportsHighSpeed() {
        return tenGigPorts != null && tenGigPorts > 0;
    }

    public String getPortSummary() {
        List<String> portTypes = new ArrayList<>();

        if (fastEthernetPorts != null && fastEthernetPorts > 0) {
            portTypes.add(fastEthernetPorts + "x FE");
        }
        if (gigabitPorts != null && gigabitPorts > 0) {
            portTypes.add(gigabitPorts + "x GE");
        }
        if (tenGigPorts != null && tenGigPorts > 0) {
            portTypes.add(tenGigPorts + "x 10GE");
        }
        if (sfpPorts != null && sfpPorts > 0) {
            portTypes.add(sfpPorts + "x SFP");
        }
        if (sfpPlusPorts != null && sfpPlusPorts > 0) {
            portTypes.add(sfpPlusPorts + "x SFP+");
        }

        return String.join(", ", portTypes);
    }

    public BigDecimal getPowerEfficiency() {
        if (powerConsumptionWatts == null || totalPorts == null || totalPorts == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(powerConsumptionWatts)
                         .divide(BigDecimal.valueOf(totalPorts), 2, java.math.RoundingMode.HALF_UP);
    }

    public String getFormFactorDescription() {
        if (formFactor == null) return "Unknown";

        StringBuilder desc = new StringBuilder(formFactor.getDisplayName());
        if (rackUnits != null && formFactor == FormFactor.RACK_MOUNT) {
            desc.append(" (").append(rackUnits).append("U)");
        }
        return desc.toString();
    }

    // Specification management
    public void addSpecification(String name, String value) {
        additionalSpecs.put(name, value);
    }

    public String getSpecification(String name) {
        return additionalSpecs.get(name);
    }

    public void addResourceUrl(String url) {
        if (url != null && !url.trim().isEmpty()) {
            resourceUrls.add(url);
        }
    }

    // Validation
    public boolean isValidModel() {
        if (manufacturer == null || manufacturer.trim().isEmpty() ||
            modelNumber == null || modelNumber.trim().isEmpty() ||
            componentType == null || totalPorts == null || totalPorts <= 0 ||
            powerConsumptionWatts == null || powerConsumptionWatts <= 0) {
            return false;
        }

        // Port count validation
        int calculatedPorts = 0;
        if (fastEthernetPorts != null) calculatedPorts += fastEthernetPorts;
        if (gigabitPorts != null) calculatedPorts += gigabitPorts;
        if (tenGigPorts != null) calculatedPorts += tenGigPorts;

        // Allow some flexibility - calculated ports can be <= total ports
        if (calculatedPorts > totalPorts) {
            return false;
        }

        // PoE validation
        if (supportsPoe && (poeBudgetWatts == null || poeBudgetWatts <= 0)) {
            return false;
        }

        // Stacking validation
        if (stackable && (maxStackUnits == null || maxStackUnits <= 1)) {
            return false;
        }

        return true;
    }

    // Factory methods for common switch types
    public static SwitchModel createAccessSwitch(String name, String manufacturer,
                                               String modelNumber, ComponentTypeEntity componentType,
                                               int gigabitPorts, int powerWatts) {
        SwitchModel model = new SwitchModel(name, manufacturer, modelNumber,
                                          componentType, gigabitPorts, powerWatts);
        model.setSwitchTier(SwitchTier.ACCESS);
        model.setGigabitPorts(gigabitPorts);
        model.setSupportsSpanningTree(true);
        model.setWebManagement(true);
        return model;
    }

    public static SwitchModel createPoeSwitch(String name, String manufacturer,
                                            String modelNumber, ComponentTypeEntity componentType,
                                            int gigabitPorts, int powerWatts, int poeBudget) {
        SwitchModel model = createAccessSwitch(name, manufacturer, modelNumber,
                                             componentType, gigabitPorts, powerWatts);
        model.setSupportsPoe(true);
        model.setPoeBudgetWatts(poeBudget);
        model.setPoeStandard(PoeStandard.POE_PLUS);
        return model;
    }

    @Override
    public String toString() {
        return manufacturer + " " + modelNumber +
               " (" + totalPorts + " ports, " +
               powerConsumptionWatts + "W" +
               (supportsPoe ? ", PoE" : "") + ")";
    }
}