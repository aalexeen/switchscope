package net.switchscope.model.component.device;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.validation.NoHtml;

import java.util.*;

/**
 * Wireless Access Point device implementation
 * Contains WiFi-specific functionality and attributes
 */
@Entity
@DiscriminatorValue("ACCESS_POINT")
@Getter
@Setter
@NoArgsConstructor
public class AccessPoint extends HasPortsImpl {

    // WiFi capabilities
    @Column(name = "wifi_standard")
    @NoHtml
    private String wifiStandard; // 802.11ac, 802.11ax (WiFi 6), etc.

    @Column(name = "max_clients")
    private Integer maxClients;

    @Column(name = "supports_dual_band", nullable = false)
    private boolean supportsDualBand = false;

    @Column(name = "supports_5ghz", nullable = false)
    private boolean supports5Ghz = false;

    @Column(name = "supports_6ghz", nullable = false)
    private boolean supports6Ghz = false;

    // Power over Ethernet
    @Column(name = "poe_required", nullable = false)
    private boolean poeRequired = true;

    @Column(name = "poe_consumption_watts")
    private Integer poeConsumptionWatts;

    // Coverage and performance
    @Column(name = "coverage_radius_meters")
    private Integer coverageRadiusMeters;

    /*@Column(name = "max_throughput_mbps")
    private Integer maxThroughputMbps;*/

    // Security features
    @Column(name = "supports_wpa3", nullable = false)
    private boolean supportsWpa3 = false;

    @Column(name = "supports_enterprise_auth", nullable = false)
    private boolean supportsEnterpriseAuth = false;

    // Management
    @Column(name = "controller_managed", nullable = false)
    private boolean controllerManaged = false;

    @Column(name = "controller_ip")
    private String controllerIp;

    // SSIDs
    @ElementCollection
    @CollectionTable(name = "access_point_ssids",
                    joinColumns = @JoinColumn(name = "access_point_id"))
    @Column(name = "ssid_name")
    private Set<String> ssids = new HashSet<>();

    // Constructors
    public AccessPoint(UUID id, String name, ComponentTypeEntity componentType) {
        super(id, name, componentType);
    }

    public AccessPoint(UUID id, String name, String manufacturer, String model,
                      String serialNumber, ComponentTypeEntity componentType) {
        super(id, name, manufacturer, model, serialNumber, componentType);
    }

    // Device abstract method implementations
    @Override
    public boolean isManaged() {
        return controllerManaged || hasManagementIp();
    }

    @Override
    public String getDefaultProtocol() {
        return controllerManaged ? "CONTROLLER" : "HTTPS";
    }

    @Override
    public String getDeviceType() {
        return "ACCESS_POINT";
    }

    @Override
    public Map<String, String> getSpecifications() {
        Map<String, String> specs = new HashMap<>();

        if (wifiStandard != null) specs.put("WiFi Standard", wifiStandard);
        if (maxClients != null) specs.put("Max Clients", maxClients.toString());
        if (supportsDualBand) specs.put("Dual Band", "Yes");
        if (supports5Ghz) specs.put("5GHz Support", "Yes");
        if (supports6Ghz) specs.put("6GHz Support", "Yes");
        if (poeRequired) {
            specs.put("PoE Required", "Yes");
            if (poeConsumptionWatts != null) specs.put("PoE Consumption", poeConsumptionWatts + "W");
        }
        if (coverageRadiusMeters != null) specs.put("Coverage Radius", coverageRadiusMeters + "m");
        //if (maxThroughputMbps != null) specs.put("Max Throughput", maxThroughputMbps + " Mbps");

        return specs;
    }

    // Access Point specific methods
    public boolean isWiFi6() {
        return "802.11ax".equals(wifiStandard) || (wifiStandard != null && wifiStandard.contains("WiFi 6"));
    }

    public boolean hasModernSecurity() {
        return supportsWpa3;
    }

    public void addSsid(String ssid) {
        if (ssid != null && !ssid.trim().isEmpty()) {
            ssids.add(ssid);
        }
    }

    public void removeSsid(String ssid) {
        ssids.remove(ssid);
    }

    // Helper method to convert Mbps to Gbps for consistency
    public void setMaxThroughputMbps(Integer maxThroughputMbps) {
        if (maxThroughputMbps != null) {
            this.maxThroughputGbps = maxThroughputMbps / 1000.0;
        }
    }

    public Integer getMaxThroughputMbps() {
        return maxThroughputGbps != null ? (int)(maxThroughputGbps * 1000) : null;
    }

    @Override
    public String toString() {
        return "AccessPoint:" + getId() + "[" + getName() +
               (getModel() != null ? ", " + getModel() : "") +
               (wifiStandard != null ? ", " + wifiStandard : "") +
               ", " + ssids.size() + " SSIDs]";
    }
}