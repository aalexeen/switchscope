package net.switchscope.repository.component;

import net.switchscope.error.DataConflictException;
import net.switchscope.error.NotFoundException;
import net.switchscope.model.component.Component;
import net.switchscope.repository.PolymorphicRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

/**
 * A repository over components, which form a parent-child tree, and therefore have to answer what
 * happens to the children when a parent is deleted.
 * <p>
 * Three answers were in the tree at once: {@code Component.childComponents} declared
 * {@code cascade = ALL, orphanRemoval = true}, the {@code parent_component_id} foreign key declared
 * {@code ON DELETE SET NULL}, and the delete itself is a bulk JPQL statement that never loads the
 * row - so no cascade could run and the schema's answer was the one that took effect. Deleting a
 * rack quietly turned the components mounted in it into free-standing ones.
 * <p>
 * The answer is now the third possibility, and it is the same one everywhere: a component with
 * children is not deleted at all. Nothing is destroyed that was not asked for, and nothing is left
 * hanging. The check lives here rather than in the nine services that delete components, because a
 * service that forgets it would be back to the old behaviour with no sign of it; overriding the two
 * delete methods means the rule cannot be skipped by not calling it.
 * <p>
 * Both deletes are overridden, so this covers the root routes as well as the leaf ones:
 * {@code DELETE /api/components/{id}} and {@code DELETE /api/devices/{id}} go through the untyped
 * pair and answer 409 for a component that holds others, exactly as the leaf-type routes do.
 *
 * @param <T> the entity type this repository is declared over, a component or a subtree of them
 */
@NoRepositoryBean
public interface ComponentTreeRepository<T extends Component> extends PolymorphicRepository<T> {

    /**
     * Counted over {@code Component} rather than over the repository's own domain type: a rack's
     * children are components of any class, and a count restricted to racks would miss the switch
     * mounted in it.
     *
     * @param id the parent's id
     * @return how many components name it as their parent
     */
    @Query("SELECT COUNT(c) FROM Component c WHERE c.parentComponent.id = :id")
    long countChildComponents(@Param("id") UUID id);

    default void refuseIfHasChildren(UUID id) {
        long children = countChildComponents(id);
        if (children > 0) {
            throw new DataConflictException("Component with id=" + id + " still holds " + children
                    + " component(s); move or delete them first");
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * The body of {@code BaseRepository.deleteExisted} is repeated rather than delegated to:
     * {@code BaseRepository} is not a direct superinterface here, so {@code super} cannot name it.
     */
    @Override
    @SuppressWarnings("all") // transaction invoked
    default void deleteExisted(UUID id) {
        refuseIfHasChildren(id);
        if (deleteByIdReturningCount(id) == 0) {
            throw new NotFoundException("Entity with id=" + id + " not found");
        }
    }

    @Override
    default void deleteExisted(UUID id, Class<? extends T> type) {
        refuseIfHasChildren(id);
        PolymorphicRepository.super.deleteExisted(id, type);
    }
}
