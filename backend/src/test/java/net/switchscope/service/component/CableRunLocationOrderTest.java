package net.switchscope.service.component;

import jakarta.persistence.EntityManager;
import net.switchscope.AbstractContextTest;
import net.switchscope.model.component.ComponentStatusEntity;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.model.component.connectivity.CableRun;
import net.switchscope.model.location.Location;
import net.switchscope.repository.component.ComponentStatusRepository;
import net.switchscope.repository.component.ComponentTypeRepository;
import net.switchscope.repository.component.connectivity.ConnectivityRepository;
import net.switchscope.repository.location.LocationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * The one association whose order is data: a cable run's path is a {@code @ManyToMany} with an
 * {@code @OrderColumn}, so reversing the ids has to reverse the stored path.
 * <p>
 * This is the case that made the resolver empty and refill the stored collection instead of
 * assigning a new one - Hibernate maintains the order column on the instance it handed out, and a
 * replacement it never saw would be a different list. Asserting it needs a real flush and a cleared
 * persistence context; a plain {@code ArrayList} would agree with any implementation.
 */
@SpringBootTest(properties = {
        "spring.testcontainers.enabled=false",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration"
})
@Transactional
class CableRunLocationOrderTest extends AbstractContextTest {

    @Autowired
    private ConnectivityRepository repository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ComponentTypeRepository componentTypeRepository;

    @Autowired
    private ComponentStatusRepository componentStatusRepository;

    @Autowired
    private ComponentReferenceResolver resolver;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("the path is stored in the order the ids were given, and re-ordering re-stores it")
    void orderSurvivesAFlush() {
        List<Location> locations = locationRepository.findAll();
        assumeTrue(locations.size() >= 2, "the seeded database has fewer than two locations");
        UUID first = locations.get(0).getId();
        UUID second = locations.get(1).getId();

        UUID id = repository.saveAndFlush(cableRunThrough(first, second)).getId();
        entityManager.clear();

        assertThat(storedPath(id)).containsExactly(first, second);

        CableRun stored = repository.getExisted(id, CableRun.class);
        resolver.applyCollection(List.of(second, first), locationRepository::findById,
                stored.getLocations(), "locationIds");
        repository.saveAndFlush(stored);
        entityManager.clear();

        assertThat(storedPath(id))
                .as("the order column follows the ids, not the order the rows were inserted in")
                .containsExactly(second, first);
    }

    private List<UUID> storedPath(UUID id) {
        return repository.getExisted(id, CableRun.class).getLocations().stream()
                .map(Location::getId)
                .toList();
    }

    private CableRun cableRunThrough(UUID... locationIds) {
        ComponentTypeEntity type = componentTypeRepository.findAll().getFirst();
        ComponentStatusEntity status = componentStatusRepository.findAll().getFirst();
        CableRun cableRun = new CableRun();
        cableRun.setName("ordered-path-test");
        cableRun.setManufacturer("test");
        cableRun.setSerialNumber("ordered-path-test");
        cableRun.setCableLengthMeters(1.0);
        cableRun.setComponentType(type);
        cableRun.setComponentStatus(status);
        resolver.applyCollection(List.of(locationIds), locationRepository::findById,
                cableRun.getLocations(), "locationIds");
        return cableRun;
    }
}
