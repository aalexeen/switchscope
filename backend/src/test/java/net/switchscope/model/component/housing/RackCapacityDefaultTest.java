package net.switchscope.model.component.housing;

import net.switchscope.model.component.catalog.housing.RackModelEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The three answers {@code supplyRackUnitsTotal} can give, none of which the endpoint test can tell
 * apart on the seeded data alone.
 * <p>
 * {@code RackCapacityEndpointTest} covers the path a request takes, and it can only reach the
 * branch where the rack type carries a capacity - the two fallbacks need a type whose capacity is
 * null and a rack with no type, and neither can be created through the API, where {@code rackTypeId}
 * is mandatory. A branch nobody exercises is a branch nobody knows the answer of.
 * <p>
 * The null the tests start from is not contrived: it is what the generated create mapping writes
 * when a payload leaves {@code rackUnitsTotal} out.
 */
class RackCapacityDefaultTest {

    @Test
    @DisplayName("a rack with no capacity takes the one its type is typically built with")
    void takesTheCapacityFromTheRackType() {
        Rack rack = rackWithoutCapacity(rackTypeOf(12));

        rack.supplyRackUnitsTotal();

        assertThat(rack.getRackUnitsTotal())
                .as("the type is the better answer than any constant, and it is the one the"
                        + " constructors have always used")
                .isEqualTo(12);
    }

    @Test
    @DisplayName("a type that says nothing about capacity, and no type at all, fall back to the default")
    void fallsBackWhenTheTypeCannotSay() {
        Rack typedButSilent = rackWithoutCapacity(rackTypeOf(null));
        typedButSilent.supplyRackUnitsTotal();

        assertThat(typedButSilent.getRackUnitsTotal())
                .as("typical_capacity_u is nullable, so consulting the type has to survive a type"
                        + " that has no capacity on it")
                .isEqualTo(Rack.DEFAULT_RACK_UNITS_TOTAL);

        Rack untyped = rackWithoutCapacity(null);
        untyped.supplyRackUnitsTotal();

        assertThat(untyped.getRackUnitsTotal())
                .as("the mapper ignores rackType, so the value is still null when the resolver has"
                        + " not run - the field is read on the way out either way")
                .isEqualTo(Rack.DEFAULT_RACK_UNITS_TOTAL);
    }

    @Test
    @DisplayName("a capacity the request did carry is left alone")
    void keepsTheCapacityItWasGiven() {
        Rack rack = rackWithoutCapacity(rackTypeOf(12));
        rack.setRackUnitsTotal(8);

        rack.supplyRackUnitsTotal();

        assertThat(rack.getRackUnitsTotal())
                .as("supplying what is missing must not overrule what was asked for, however odd"
                        + " the number looks next to the type")
                .isEqualTo(8);
    }

    /** A rack in the state the create mapping leaves it in: the capacity assigned as null. */
    private static Rack rackWithoutCapacity(RackModelEntity rackType) {
        Rack rack = new Rack();
        rack.setRackUnitsTotal(null);
        rack.setRackType(rackType);
        return rack;
    }

    private static RackModelEntity rackTypeOf(Integer typicalCapacityU) {
        RackModelEntity type = new RackModelEntity();
        type.setTypicalCapacityU(typicalCapacityU);
        return type;
    }
}
