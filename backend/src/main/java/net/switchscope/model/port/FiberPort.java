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
 * Fiber Port implementation - represents fiber optic network ports
 */
@Entity
@DiscriminatorValue("FIBER")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FiberPort extends Port {

    @Column(name = "fiber_type")
    private String fiberType; // SINGLE_MODE, MULTI_MODE

    @Column(name = "wavelength_nm")
    private Integer wavelengthNm; // Wavelength in nanometers

    @Column(name = "fiber_standard")
    private String fiberStandard; // 1000BASE-SX, 1000BASE-LX, 10GBASE-SR, etc.

    @Column(name = "transmission_distance_km")
    private Double transmissionDistanceKm;

    @Column(name = "tx_power_dbm")
    private Double txPowerDbm; // Transmit power in dBm

    @Column(name = "rx_power_dbm")
    private Double rxPowerDbm; // Receive power in dBm

    @Column(name = "optical_loss_db")
    private Double opticalLossDb;

    // Constructors
    public FiberPort(UUID id, String name, Device device, Integer portNumber) {
        super(id, name, device, portNumber);
        this.setMediumType("FIBER");
    }

    public FiberPort(UUID id, String name, String description, Device device,
                    Integer portNumber, String fiberType, String fiberStandard) {
        super(id, name, description, device, portNumber);
        this.fiberType = fiberType;
        this.fiberStandard = fiberStandard;
        this.setMediumType("FIBER");
        setDefaultConnectorType();
    }

    @Override
    public String getPortType() {
        return "FIBER";
    }

    @Override
    public Map<String, String> getSpecifications() {
        Map<String, String> specs = getCommonSpecifications();

        if (fiberType != null) specs.put("Fiber Type", fiberType);
        if (fiberStandard != null) specs.put("Standard", fiberStandard);
        if (wavelengthNm != null) specs.put("Wavelength", wavelengthNm + "nm");
        if (transmissionDistanceKm != null) specs.put("Max Distance", transmissionDistanceKm + "km");
        if (txPowerDbm != null) specs.put("TX Power", txPowerDbm + "dBm");
        if (rxPowerDbm != null) specs.put("RX Power", rxPowerDbm + "dBm");
        if (opticalLossDb != null) specs.put("Optical Loss", opticalLossDb + "dB");

        return specs;
    }

    @Override
    public boolean supportsSpeed(Long speedMbps) {
        if (speedMbps == null) return false;

        // Common fiber speeds
        return speedMbps == 1000L || speedMbps == 10000L ||
               speedMbps == 25000L || speedMbps == 40000L ||
               speedMbps == 100000L;
    }

    @Override
    public boolean isCompatibleWith(String connectorType) {
        return "LC".equals(connectorType) || "SC".equals(connectorType) ||
               "ST".equals(connectorType) || "SFP".equals(connectorType) ||
               "SFP+".equals(connectorType) || "QSFP".equals(connectorType) ||
               "QSFP+".equals(connectorType) || "QSFP28".equals(connectorType);
    }

    // Business methods
    public boolean isSingleMode() {
        return "SINGLE_MODE".equals(fiberType);
    }

    public boolean isMultiMode() {
        return "MULTI_MODE".equals(fiberType);
    }

    public boolean hasOpticalSignal() {
        return rxPowerDbm != null && rxPowerDbm > -40.0; // Threshold for signal presence
    }

    public boolean isOpticalLinkHealthy() {
        if (txPowerDbm == null || rxPowerDbm == null) return false;

        double signalLoss = txPowerDbm - rxPowerDbm;
        return signalLoss <= 15.0; // Acceptable loss threshold
    }

    public String getOpticalLinkQuality() {
        if (!hasOpticalSignal()) return "No Signal";
        if (!isOpticalLinkHealthy()) return "Poor";

        if (opticalLossDb != null) {
            if (opticalLossDb <= 3.0) return "Excellent";
            if (opticalLossDb <= 6.0) return "Good";
            if (opticalLossDb <= 10.0) return "Fair";
        }

        return "Unknown";
    }

    private void setDefaultConnectorType() {
        if (fiberStandard != null) {
            if (fiberStandard.contains("10GBASE") || fiberStandard.contains("25GBASE")) {
                this.setConnectorType("SFP+");
            } else if (fiberStandard.contains("40GBASE") || fiberStandard.contains("100GBASE")) {
                this.setConnectorType("QSFP+");
            } else {
                this.setConnectorType("SFP");
            }
        } else {
            this.setConnectorType("LC");
        }
    }
}