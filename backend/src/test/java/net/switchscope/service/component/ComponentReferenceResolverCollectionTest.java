package net.switchscope.service.component;

import net.switchscope.error.IllegalRequestDataException;
import net.switchscope.error.NotFoundException;
import net.switchscope.model.location.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * The rules a collection of ids follows, which until now it did not follow at all: five DTO fields
 * carried ids into services that ignored them, so a payload naming locations for a cable run was
 * accepted, answered with the stored set, and changed nothing.
 * <p>
 * The repositories are stubbed by a map rather than mocked: what is under test is which ids reach
 * the collection and in what order, not how they are loaded.
 */
class ComponentReferenceResolverCollectionTest {

    private static final UUID FIRST = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID SECOND = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID ABSENT = UUID.fromString("00000000-0000-0000-0000-0000000000ff");

    private final Map<UUID, Location> stored = Map.of(FIRST, named("first"), SECOND, named("second"));
    private final ComponentReferenceResolver resolver =
            new ComponentReferenceResolver(null, null, null, null, null, null);

    @Test
    @DisplayName("a field the payload did not mention leaves the association alone")
    void nullMeansUntouched() {
        List<Location> target = new ArrayList<>(List.of(stored.get(FIRST)));

        resolver.applyCollection(null, this::find, target, "locationIds");

        assertThat(target).containsExactly(stored.get(FIRST));
    }

    @Test
    @DisplayName("an empty collection is a request to detach everything")
    void emptyMeansCleared() {
        List<Location> target = new ArrayList<>(List.of(stored.get(FIRST)));

        resolver.applyCollection(List.of(), this::find, target, "locationIds");

        assertThat(target).isEmpty();
    }

    @Test
    @DisplayName("the order the ids were given is the order stored - a cable run is a path")
    void orderIsPreserved() {
        List<Location> target = new ArrayList<>();

        resolver.applyCollection(List.of(SECOND, FIRST), this::find, target, "locationIds");

        assertThat(target).containsExactly(stored.get(SECOND), stored.get(FIRST));
    }

    @Test
    @DisplayName("one id that does not exist leaves the association as it was, not half-applied")
    void unknownIdChangesNothing() {
        List<Location> target = new ArrayList<>(List.of(stored.get(FIRST)));

        assertThatThrownBy(() -> resolver.applyCollection(
                List.of(SECOND, ABSENT), this::find, target, "locationIds"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("locationIds");

        assertThat(target).containsExactly(stored.get(FIRST));
    }

    @Test
    @DisplayName("a null inside the collection is the caller's mistake, not a lookup")
    void nullIdIsRejected() {
        List<UUID> ids = new ArrayList<>();
        ids.add(null);

        assertThatThrownBy(() -> resolver.applyCollection(
                ids, this::find, new ArrayList<Location>(), "locationIds"))
                .isInstanceOf(IllegalRequestDataException.class);
    }

    private Optional<Location> find(UUID id) {
        return Optional.ofNullable(stored.get(id));
    }

    private static Location named(String name) {
        Location location = new Location();
        location.setName(name);
        return location;
    }
}
