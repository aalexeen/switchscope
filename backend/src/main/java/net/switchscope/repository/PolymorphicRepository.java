package net.switchscope.repository;

import net.switchscope.error.NotFoundException;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * A repository whose domain type is a root of the single-table inheritance hierarchy while its
 * callers each serve one leaf type.
 * <p>
 * The inherited {@link BaseRepository#deleteExisted(UUID)} is a bulk JPQL delete over
 * {@code #{#entityName}}, and on such a repository that name is the root: it removes any row of
 * the table by id, whatever the discriminator says. With {@code HousingRepository} typed as
 * {@code BaseRepository<Component>}, {@code DELETE /api/housing/racks/{id}} deleted a switch as
 * readily as a rack, and {@code getExisted} handed the service a component of the wrong class,
 * which its cast turned into a 500 rather than a 404.
 * <p>
 * An id carries no type, so the expected type has to come from the caller - hence the
 * {@code Class} argument on the two methods below, and hence the refusal of the untyped pair
 * rather than a silent inheritance of it.
 *
 * @param <T> the root entity type of the hierarchy this repository spans
 */
@NoRepositoryBean
public interface PolymorphicRepository<T> extends BaseRepository<T> {

    @Query("SELECT e FROM #{#entityName} e WHERE e.id = :id AND TYPE(e) = :type")
    Optional<T> findByIdAndType(@Param("id") UUID id, @Param("type") Class<? extends T> type);

    @Transactional
    @Modifying
    @Query("DELETE FROM #{#entityName} e WHERE e.id = :id AND TYPE(e) = :type")
    int deleteByIdAndTypeReturningCount(@Param("id") UUID id, @Param("type") Class<? extends T> type);

    /**
     * @param id   the entity id
     * @param type the exact type the caller expects, subtypes of it do not match
     * @return the entity, already of the requested type
     * @throws NotFoundException if no row has that id, or the row that has it is of another type
     */
    default <X extends T> X getExisted(UUID id, Class<X> type) {
        return findByIdAndType(id, type)
                .map(type::cast)
                .orElseThrow(() -> new NotFoundException(
                        type.getSimpleName() + " with id=" + id + " not found"));
    }

    /**
     * @param id   the entity id
     * @param type the exact type the caller expects, subtypes of it do not match
     * @throws NotFoundException if no row has that id, or the row that has it is of another type
     */
    @SuppressWarnings("all") // transaction invoked
    default void deleteExisted(UUID id, Class<? extends T> type) {
        if (deleteByIdAndTypeReturningCount(id, type) == 0) {
            throw new NotFoundException(type.getSimpleName() + " with id=" + id + " not found");
        }
    }

}
