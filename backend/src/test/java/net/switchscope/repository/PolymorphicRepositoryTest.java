package net.switchscope.repository;

import jakarta.persistence.EntityManager;
import net.switchscope.AbstractContextTest;
import net.switchscope.error.NotFoundException;
import net.switchscope.model.component.Component;
import net.switchscope.model.component.ComponentStatusEntity;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.model.component.device.Device;
import net.switchscope.model.component.device.NetworkSwitch;
import net.switchscope.model.component.device.Router;
import net.switchscope.model.component.housing.Rack;
import net.switchscope.repository.component.ComponentRepository;
import net.switchscope.repository.component.ComponentStatusRepository;
import net.switchscope.repository.component.ComponentTypeRepository;
import net.switchscope.repository.component.device.DeviceRepository;
import net.switchscope.repository.component.housing.HousingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * A repository that spans a single-table hierarchy must not delete across it.
 * <p>
 * {@code HousingRepository} and {@code ConnectivityRepository} are typed on {@code Component} and
 * {@code DeviceRepository} on {@code Device}, while the controllers on top of them serve one leaf
 * type each. The inherited delete is a bulk JPQL statement over the repository's domain type, so
 * before {@link PolymorphicRepository} {@code DELETE /api/housing/racks/{id}} removed whatever row
 * carried that id - a switch included - and the matching {@code get} threw a ClassCastException
 * that surfaced as 500 rather than 404.
 * <p>
 * The test runs inside a rolled-back transaction and clears the persistence context before every
 * assertion about what is still stored: a bulk delete does not touch managed instances, so a
 * {@code findById} served from the first-level cache would report a row that is no longer there.
 */
@SpringBootTest(properties = {
        "spring.testcontainers.enabled=false",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration"
})
@Transactional
class PolymorphicRepositoryTest extends AbstractContextTest {

    @Autowired
    private HousingRepository housingRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private ComponentRepository componentRepository;

    @Autowired
    private ComponentTypeRepository componentTypeRepository;

    @Autowired
    private ComponentStatusRepository componentStatusRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("the rack repository refuses to delete a switch by its id, and leaves it stored")
    void deleteDoesNotCrossFromRacksToSwitches() {
        UUID switchId = anyNetworkSwitchId();

        assertThatThrownBy(() -> housingRepository.deleteExisted(switchId, Rack.class))
                .as("the id belongs to a switch, so for the rack routes it does not exist")
                .isInstanceOf(NotFoundException.class);

        entityManager.clear();
        assertThat(componentRepository.findById(switchId))
                .as("and the switch must still be there - this is the assertion that failed before")
                .isPresent();
    }

    @Test
    @DisplayName("the same holds inside the device hierarchy: a switch is not a router")
    void deleteDoesNotCrossBetweenDeviceTypes() {
        UUID switchId = anyNetworkSwitchId();

        assertThatThrownBy(() -> deviceRepository.deleteExisted(switchId, Router.class))
                .isInstanceOf(NotFoundException.class);

        entityManager.clear();
        assertThat(componentRepository.findById(switchId)).isPresent();
    }

    @Test
    @DisplayName("reading across the hierarchy is a 404 rather than a cast failure")
    void getDoesNotCrossTypes() {
        UUID switchId = anyNetworkSwitchId();

        assertThatThrownBy(() -> housingRepository.getExisted(switchId, Rack.class))
                .isInstanceOf(NotFoundException.class);

        assertThatCode(() -> deviceRepository.getExisted(switchId, NetworkSwitch.class))
                .as("the type filter must not get in the way of the caller that owns the type")
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("a rack is still deletable through the rack repository")
    void deleteOfTheOwnTypeStillWorks() {
        Rack rack = housingRepository.saveAndFlush(newRack());
        entityManager.clear();

        housingRepository.deleteExisted(rack.getId(), Rack.class);

        entityManager.clear();
        assertThat(componentRepository.findById(rack.getId())).isEmpty();
    }

    private UUID anyNetworkSwitchId() {
        List<Device> switches = deviceRepository.findNetworkSwitches();
        assumeTrue(!switches.isEmpty(), "the seeded database has no network switch to test with");
        UUID id = switches.getFirst().getId();
        entityManager.clear();
        return id;
    }

    private Rack newRack() {
        ComponentTypeEntity type = componentTypeRepository.findAll().getFirst();
        ComponentStatusEntity status = componentStatusRepository.findAll().getFirst();
        Rack rack = new Rack();
        rack.setName("polymorphic-delete-test");
        rack.setManufacturer("test");
        rack.setSerialNumber("polymorphic-delete-test");
        rack.setComponentType(type);
        rack.setComponentStatus(status);
        return rack;
    }

    /**
     * The reason the typed pair exists at all. The inherited untyped delete is kept - the
     * controllers that serve the root itself mean exactly "whatever row has this id" - so nothing
     * stops a leaf-type caller from reaching for it, and this is what happens when one does.
     */
    @Test
    @DisplayName("the inherited untyped delete is what a leaf-type caller must not reach for")
    void untypedDeleteCrossesTheHierarchy() {
        UUID switchId = anyNetworkSwitchId();

        ((BaseRepository<Component>) housingRepository).deleteExisted(switchId);

        entityManager.clear();
        assertThat(componentRepository.findById(switchId))
                .as("the rack repository deleted a switch, which is the defect the typed pair fixes")
                .isEmpty();
    }
}
