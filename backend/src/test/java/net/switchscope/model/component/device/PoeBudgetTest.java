package net.switchscope.model.component.device;

import net.switchscope.model.port.EthernetPort;
import net.switchscope.model.port.Port;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * What a switch answers for its remaining PoE budget when a port has PoE turned on but has never
 * reported what it draws.
 * <p>
 * {@code poe_power_watts} is the <em>current</em> consumption and is nullable: a port can be
 * PoE-enabled and silent, which is exactly what {@code Port.enablePoe} leaves behind - it sets
 * {@code poeEnabled} and the maximum, and nothing at all for the reading. The sum filtered on
 * {@code isPoeCapable}, which only asks whether PoE is switched on, and then unboxed the reading,
 * so one such port turned {@code availablePoeBudget} - a field every switch response carries - into
 * a NullPointerException.
 */
class PoeBudgetTest {

    @Test
    @DisplayName("a PoE port that has not reported its draw counts as drawing nothing")
    void silentPortCountsAsZero() {
        NetworkSwitch sw = poeSwitch(740);
        sw.addPort(poePort(12.5));
        sw.addPort(poePort(null));

        assertThat(sw.getAvailablePoeBudget())
                .as("the port draws an unknown amount, and an unknown amount is not the whole"
                        + " budget - the alternative was answering 500 for the whole switch")
                .isEqualTo(727.5);
    }

    @Test
    @DisplayName("only ports with PoE switched on are counted")
    void portsWithoutPoeAreNotCounted() {
        NetworkSwitch sw = poeSwitch(100);
        Port off = poePort(30.0);
        off.setPoeEnabled(false);
        sw.addPort(off);
        sw.addPort(poePort(10.0));

        assertThat(sw.getAvailablePoeBudget())
                .as("a reading left on a port whose PoE is off is not consumption")
                .isEqualTo(90.0);
    }

    @Test
    @DisplayName("a switch without a PoE budget has nothing to spend")
    void switchWithoutPoeAnswersZero() {
        NetworkSwitch sw = new NetworkSwitch();
        sw.setSupportsPoe(false);
        sw.addPort(poePort(10.0));

        assertThat(sw.getAvailablePoeBudget()).isZero();
    }

    private static NetworkSwitch poeSwitch(int budgetWatts) {
        NetworkSwitch sw = new NetworkSwitch();
        sw.setSupportsPoe(true);
        sw.setPoeBudgetWatts(budgetWatts);
        return sw;
    }

    /** A port with PoE switched on, reporting {@code watts} - or reporting nothing, for null. */
    private static Port poePort(Double watts) {
        EthernetPort port = new EthernetPort();
        port.setPoeEnabled(true);
        port.setPoePowerWatts(watts);
        return port;
    }
}
