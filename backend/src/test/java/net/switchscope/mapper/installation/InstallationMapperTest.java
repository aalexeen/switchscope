package net.switchscope.mapper.installation;

import net.switchscope.model.installation.Installation;
import net.switchscope.to.installation.InstallationTo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * A read-only collection getter is not a mapping target.
 * <p>
 * {@code Installation.getOccupiedRackPositions()} computes its answer from {@code rackPosition} and
 * {@code rackUnitHeight}, and returns {@code List.of()} when the installation is not rack-mounted.
 * MapStruct saw a getter of a collection type and generated {@code entity.getOccupiedRackPositions()
 * .clear()} into the update method, so <em>every</em> write to an installation died on
 * {@code UnsupportedOperationException} - a 500 on {@code PUT /api/installations/{id}} for any
 * payload at all, including {@code {}}. Confirmed against the code as it stood before the fix.
 * <p>
 * The test is on the mapper rather than the endpoint because that is where the mistake is
 * expressible: the fix is one {@code @Mapping(ignore = true)}, and it is undone by deleting it.
 */
class InstallationMapperTest {

    private final InstallationMapper mapper = Mappers.getMapper(InstallationMapper.class);

    @Test
    @DisplayName("updating a non-rack-mounted installation does not touch the computed positions")
    void updateDoesNotWriteToTheComputedCollection() {
        Installation entity = new Installation();
        entity.setInstallationNotes("before");
        InstallationTo to = new InstallationTo();
        to.setInstallationNotes("after");

        assertThatCode(() -> mapper.updateFromTo(entity, to))
                .as("rackPosition is null, so getOccupiedRackPositions() returns List.of() and any"
                        + " attempt to clear it throws")
                .doesNotThrowAnyException();
        assertThat(entity.getInstallationNotes())
                .as("and the update it was asked to do still happened")
                .isEqualTo("after");
    }

    @Test
    @DisplayName("creating one does not either")
    void createDoesNotWriteToTheComputedCollection() {
        InstallationTo to = new InstallationTo();
        to.setInstallationNotes("new");

        assertThatCode(() -> mapper.toEntity(to)).doesNotThrowAnyException();
    }
}
