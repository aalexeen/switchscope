package net.switchscope.model.installation;

import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.model.component.housing.Rack;
import net.switchscope.model.location.Location;
import net.switchscope.model.location.catalog.LocationTypeEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Two checks in {@code Installation} that could not pass and one that could not run.
 * <p>
 * {@code isValidLocationInstallation} asked the housing component {@code canContainComponent(null)},
 * which is false by definition, so every installation housed in a component was invalid.
 * {@code fitsInLocation} added {@code rackPosition} to a height without looking at it first, so an
 * item with a declared height and no position - anything not yet placed - threw instead of
 * answering. {@code isValidRackPosition} dereferenced the location it had not checked.
 * <p>
 * Nothing calls {@code isValidInstallation} today, which is why none of this surfaced as a bug
 * report; it is also why the fix is the declarations rather than a wiring change.
 */
class InstallationValidityTest {

    @Test
    @DisplayName("an installation housed in a component that can hold one is valid")
    void housedInAComponentThatCanHold() {
        Installation installation = installationAt(rackLikeLocation(42));
        installation.setComponent(componentThatHolds(true));

        assertThat(installation.isValidLocationInstallation())
                .as("the housing component's type allows containment; there is nothing to refuse")
                .isTrue();
    }

    @Test
    @DisplayName("and one housed in a component that cannot hold anything is not")
    void housedInAComponentThatCannotHold() {
        Installation installation = installationAt(rackLikeLocation(42));
        installation.setComponent(componentThatHolds(false));

        assertThat(installation.isValidLocationInstallation()).isFalse();
    }

    @Test
    @DisplayName("a height without a position is answered, not thrown at")
    void heightWithoutPositionDoesNotThrow() {
        Installation installation = installationAt(rackLikeLocation(42));
        installation.setRackUnitHeight(2);

        assertThatCode(installation::fitsInLocation).doesNotThrowAnyException();
        assertThat(installation.fitsInLocation())
                .as("nothing has claimed a position, so nothing overflows the rack")
                .isTrue();
    }

    @Test
    @DisplayName("with a position, the item still has to end inside the rack")
    void positionedItemMustFit() {
        Installation fits = installationAt(rackLikeLocation(42));
        fits.setRackUnitHeight(2);
        fits.setRackPosition(41);
        assertThat(fits.fitsInLocation()).isTrue();

        Installation overflows = installationAt(rackLikeLocation(42));
        overflows.setRackUnitHeight(3);
        overflows.setRackPosition(41);
        assertThat(overflows.fitsInLocation()).isFalse();
    }

    @Test
    @DisplayName("a rack position without a location is refused rather than dereferenced")
    void positionWithoutLocation() {
        Installation installation = new Installation();
        installation.setRackPosition(1);

        assertThatCode(installation::isValidRackPosition).doesNotThrowAnyException();
        assertThat(installation.isValidRackPosition()).isFalse();
    }

    private static Installation installationAt(Location location) {
        Installation installation = new Installation();
        installation.setLocation(location);
        return installation;
    }

    private static Location rackLikeLocation(int rackUnits) {
        LocationTypeEntity type = new LocationTypeEntity();
        type.setRackLike(true);
        type.setCanHoldEquipment(true);
        type.setDefaultRackUnits(rackUnits);
        Location location = new Location();
        location.setType(type);
        return location;
    }

    private static Rack componentThatHolds(boolean canHold) {
        ComponentTypeEntity type = new ComponentTypeEntity();
        type.setCanContainComponents(canHold);
        Rack rack = new Rack();
        rack.setComponentType(type);
        return rack;
    }
}
