package net.switchscope.model.port;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.switchscope.model.component.device.Device;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Ethernet Port implementation - represents standard Ethernet network ports
 */
@Entity
@DiscriminatorValue("ETHERNET")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EthernetPort extends Port {

    @Column(name = "ethernet_standard")
    private String ethernetStandard; // 10BASE-T, 100BASE-TX, 1000BASE-T, etc.

    @Column(name = "mdi_mdix_mode")
    private String mdiMdixMode; // MDI, MDIX, AUTO

    @Column(name = "cable_length_meters")
    private Integer cableLengthMeters;

    @Column(name = "link_partner_info")
    private String linkPartnerInfo;

    // Constructors
    public EthernetPort(UUID id, String name, Device device, Integer portNumber) {
        super(id, name, device, portNumber);
        this.setConnectorType("RJ45");
        this.setMediumType("COPPER");
    }

    public EthernetPort(UUID id, String name, String description, Device device,
                       Integer portNumber, String ethernetStandard) {
        super(id, name, description, device, portNumber);
        this.ethernetStandard = ethernetStandard;
        this.setConnectorType("RJ45");
        this.setMediumType("COPPER");
    }

    @Override
    public String getPortType() {
        return "ETHERNET";
    }

    @Override
    public Map<String, String> getSpecifications() {
        Map<String, String> specs = getCommonSpecifications();

        if (ethernetStandard != null) specs.put("Standard", ethernetStandard);
        if (mdiMdixMode != null) specs.put("MDI/MDIX", mdiMdixMode);
        if (cableLengthMeters != null) specs.put("Cable Length", cableLengthMeters + "m");
        if (linkPartnerInfo != null) specs.put("Link Partner", linkPartnerInfo);

        return specs;
    }

    @Override
    public boolean supportsSpeed(Long speedMbps) {
        if (speedMbps == null) return false;

        // Standard Ethernet speeds
        return speedMbps == 10L || speedMbps == 100L ||
               speedMbps == 1000L || speedMbps == 10000L;
    }

    @Override
    public boolean isCompatibleWith(String connectorType) {
        return "RJ45".equals(connectorType) || "RJ11".equals(connectorType);
    }

    // Business methods
    public boolean isGigabitCapable() {
        return getMaxSpeedMbps() != null && getMaxSpeedMbps() >= 1000L;
    }

    public boolean is10GigabitCapable() {
        return getMaxSpeedMbps() != null && getMaxSpeedMbps() >= 10000L;
    }

    public String getEthernetClass() {
        Long maxSpeed = getMaxSpeedMbps();
        if (maxSpeed == null) return "Unknown";

        if (maxSpeed >= 10000L) return "10 Gigabit";
        if (maxSpeed >= 1000L) return "Gigabit";
        if (maxSpeed >= 100L) return "Fast Ethernet";
        if (maxSpeed >= 10L) return "Ethernet";

        return "Unknown";
    }
}