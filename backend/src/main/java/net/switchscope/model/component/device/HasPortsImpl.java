package net.switchscope.model.component.device;

import jakarta.persistence.*;
import net.switchscope.model.component.ComponentStatusEntity;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.model.location.Location;
import net.switchscope.model.port.Port;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Abstract base class for devices that have network ports
 * Contains common port management functionality
 */
@MappedSuperclass
public abstract class HasPortsImpl extends Device implements HasPorts {

    // Common performance characteristics
    @Column(name = "max_throughput_gbps")
    protected Double maxThroughputGbps;

    // Port relationships (common for devices with ports)
    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    protected List<Port> ports = new ArrayList<>();

    // Constructors
    protected HasPortsImpl() {
        super();
    }

    protected HasPortsImpl(UUID id, String name, ComponentTypeEntity componentType) {
        super(id, name, componentType);
    }

    protected HasPortsImpl(UUID id, String name, String manufacturer, String model,
                          String serialNumber, ComponentTypeEntity componentType) {
        super(id, name, manufacturer, model, serialNumber, componentType);
    }

    protected HasPortsImpl(UUID id, String name, String manufacturer, String model,
                          String serialNumber, ComponentStatusEntity status, Location location,
                          ComponentTypeEntity componentType) {
        super(id, name, manufacturer, model, serialNumber, status, location, componentType);
    }

    // HasPorts interface implementation (common for all port-enabled devices)
    @Override
    public List<Port> getPorts() {
        return new ArrayList<>(ports);
    }

    @Override
    public void addPort(Port port) {
        if (port != null && !ports.contains(port)) {
            ports.add(port);
            port.setDevice(this);
        }
    }

    @Override
    public void removePort(Port port) {
        if (port != null && ports.contains(port)) {
            ports.remove(port);
            port.setDevice(null);
        }
    }

    @Override
    public Port getPortByNumber(int portNumber) {
        return ports.stream()
                .filter(port -> port.getPortNumber() == portNumber)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Port> getAvailablePorts() {
        return ports.stream()
                .filter(Port::isAvailable)
                .toList();
    }

    @Override
    public int getPortCount() {
        return ports.size();
    }

    @Override
    public int getAvailablePortCount() {
        return (int) ports.stream().filter(Port::isAvailable).count();
    }

    // Device abstract method implementation
    @Override
    public boolean hasPorts() {
        return true;
    }

    // Common getters and setters
    public Double getMaxThroughputGbps() {
        return maxThroughputGbps;
    }

    public void setMaxThroughputGbps(Double maxThroughputGbps) {
        this.maxThroughputGbps = maxThroughputGbps;
    }

    protected void setPorts(List<Port> ports) {
        this.ports = ports != null ? ports : new ArrayList<>();
    }

    // Common helper methods
    protected String getPortsInfo() {
        return ports.size() + " ports";
    }

    protected String getThroughputInfo() {
        return maxThroughputGbps != null ? maxThroughputGbps + " Gbps" : "Unknown throughput";
    }

    // Enhanced validation
    @Override
    public boolean isValidConfiguration() {
        if (!super.isValidConfiguration()) {
            return false;
        }

        // Basic port validation
        if (ports.stream().anyMatch(port -> port.getDevice() != this)) {
            return false;
        }

        return true;
    }
}