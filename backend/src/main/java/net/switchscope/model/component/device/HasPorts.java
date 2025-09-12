package net.switchscope.model.component.device;

import net.switchscope.model.port.Port;
import java.util.List;

/**
 * Interface for components that have network ports
 */
public interface HasPorts {

    /**
     * Get all ports of this component
     * @return immutable list of ports
     */
    List<Port> getPorts();

    /**
     * Add a port to this component
     * @param port the port to add
     */
    void addPort(Port port);

    /**
     * Remove a port from this component
     * @param port the port to remove
     */
    void removePort(Port port);

    /**
     * Get port by its number
     * @param portNumber the port number
     * @return the port or null if not found
     */
    Port getPortByNumber(int portNumber);

    /**
     * Get all available (not connected) ports
     * @return list of available ports
     */
    List<Port> getAvailablePorts();

    /**
     * Get total number of ports
     * @return port count
     */
    int getPortCount();

    /**
     * Get number of available ports
     * @return available port count
     */
    int getAvailablePortCount();

    /**
     * Check if component has any ports
     * @return true if has ports
     */
    default boolean hasPorts() {
        return getPortCount() > 0;
    }

    /**
     * Check if component has available ports
     * @return true if has available ports
     */
    default boolean hasAvailablePorts() {
        return getAvailablePortCount() > 0;
    }

    /**
     * Get port utilization percentage
     * @return utilization as percentage (0-100)
     */
    default double getPortUtilization() {
        int total = getPortCount();
        if (total == 0) {
            return 0.0;
        }
        int used = total - getAvailablePortCount();
        return (used * 100.0) / total;
    }

    /**
     * Check if all ports are used
     * @return true if no available ports
     */
    default boolean isFullyUtilized() {
        return getAvailablePortCount() == 0 && getPortCount() > 0;
    }
}