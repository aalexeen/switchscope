package net.switchscope.model.component.catalog;

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
 * Switch Model Catalog Entry - represents a specific switch model from a manufacturer
 */
@Entity
@Table(name = "switch_models",
       uniqueConstraints = @UniqueConstraint(columnNames = {"manufacturer", "model_number"}))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SwitchModel extends DeviceModel {

    // Switch-specific fields only (common fields moved to DeviceModel)

    // Switch classification (using String instead of enum)
    @Column(name = "switch_class")
    @Size(max = 32)
    @NoHtml
    private String switchClass = "MANAGED";

    @Column(name = "switch_tier")
    @Size(max = 32)
    @NoHtml
    private String switchTier = "ACCESS";

    @Column(name = "is_stackable", nullable = false)
    private boolean stackable = false;

    @Column(name = "max_stack_units")
    private Integer maxStackUnits;

    // Physical specifications (switch-specific)
    @Column(name = "rack_units", nullable = false)
    @NotNull
    @Min(1) @Max(10)
    private Integer rackUnits = 1;

    @Column(name = "form_factor")
    @Size(max = 32)
    @NoHtml
    private String formFactor = "RACK_MOUNT";

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

    // PoE specifications
    @Column(name = "supports_poe", nullable = false)
    private boolean supportsPoe = false;

    @Column(name = "poe_budget_watts")
    private Integer poeBudgetWatts;

    @Column(name = "poe_standard")
    @Size(max = 32)
    @NoHtml
    private String poeStandard;

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

    // Constructor
    public SwitchModel(String name, String manufacturer, String modelNumber,
                      ComponentTypeEntity componentType, Integer totalPorts,
                      Integer powerConsumptionWatts) {
        super(name, manufacturer, modelNumber, componentType, powerConsumptionWatts);
        this.totalPorts = totalPorts;
    }

    // Implementation of abstract methods from DeviceModel
    @Override
    public BigDecimal getPowerEfficiency() {
        if (getPowerConsumptionWatts() == null || totalPorts == null || totalPorts == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(getPowerConsumptionWatts())
                         .divide(BigDecimal.valueOf(totalPorts), 2, java.math.RoundingMode.HALF_UP);
    }

    @Override
    public String getDeviceTypeSummary() {
        return getPortSummary() +
               (supportsPoe ? ", PoE" : "") +
               (stackable ? ", Stackable" : "");
    }

    // Switch-specific business methods
    public boolean isManaged() {
        return !"UNMANAGED".equals(switchClass);
    }

    public boolean isEnterprise() {
        return "CORE".equals(switchTier) || "DISTRIBUTION".equals(switchTier);
    }

    public boolean hasPoePlus() {
        return supportsPoe && poeStandard != null &&
               (poeStandard.contains("POE_PLUS") || poeStandard.contains("802.3at"));
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

    // Enhanced validation specific to switches
    @Override
    public boolean isValidModel() {
        if (!super.isValidModel()) {
            return false;
        }

        if (totalPorts == null || totalPorts <= 0) {
            return false;
        }

        // Port count validation
        int calculatedPorts = 0;
        if (fastEthernetPorts != null) calculatedPorts += fastEthernetPorts;
        if (gigabitPorts != null) calculatedPorts += gigabitPorts;
        if (tenGigPorts != null) calculatedPorts += tenGigPorts;

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

    @Override
    public String toString() {
        return getManufacturer() + " " + getModelNumber() +
               " (" + totalPorts + " ports, " + getPowerConsumptionWatts() + "W" +
               (supportsPoe ? ", PoE" : "") + ")";
    }
}