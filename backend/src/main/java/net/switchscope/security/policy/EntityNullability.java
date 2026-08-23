package net.switchscope.security.policy;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Whether a property of a persistent entity can hold {@code null} at all.
 * <p>
 * The field-access layer answers a different question - <em>may this caller</em> clear the field -
 * and answers it from annotations on the <em>DTO</em>. That is not enough to clear a field safely:
 * {@link FieldAccessLevel#ADMIN_NULLABLE} is the default for anything unannotated, so a DTO nobody
 * has annotated (as {@code LocationTo} was) declares its entire surface admin-clearable, NOT NULL
 * columns included. Writing those nulls would produce a constraint violation - a 500 for a request
 * the API had just accepted as legitimate. So permission to clear is checked on the DTO and
 * possibility of clearing is checked here, on the entity, and a field needs both.
 * <p>
 * Read from the JPA mapping rather than from the database: the mapping is what Hibernate will
 * flush, it is available without a connection, and it is where a developer changing nullability
 * would look. The three kinds of impossible are all reported, because "cannot be cleared" and
 * "cannot be cleared <em>because</em>" are different messages to be on the receiving end of.
 */
@Component
public class EntityNullability {

    /** Cached per entity class and property; the answer is fixed by the mapping. */
    private final Map<Class<?>, Map<String, String>> cache = new ConcurrentHashMap<>();

    /**
     * Why this property cannot be set to null, or {@code null} if it can.
     *
     * @param entityClass  the entity class
     * @param propertyName the property name
     * @return the reason it cannot be cleared, or {@code null} when clearing is possible
     */
    public String reasonItCannotBeNull(Class<?> entityClass, String propertyName) {
        // "" stands for "no reason, it can be null": the map cannot hold a null value, and the
        // absence of an entry has to keep meaning "not looked at yet".
        String reason = cache.computeIfAbsent(entityClass, ignored -> new ConcurrentHashMap<>())
                .computeIfAbsent(propertyName, name -> {
                    String found = examine(entityClass, name);
                    return found == null ? "" : found;
                });
        return reason.isEmpty() ? null : reason;
    }

    /**
     * Whether the property can be set to null.
     *
     * @param entityClass  the entity class
     * @param propertyName the property name
     * @return true when clearing is possible
     */
    public boolean canBeNull(Class<?> entityClass, String propertyName) {
        return reasonItCannotBeNull(entityClass, propertyName) == null;
    }

    /**
     * Whether the entity stores this property in a field of its own.
     * <p>
     * A setter alone is not enough to go on: an entity may expose one for a value it derives, and
     * clearing that would write to something the schema knows nothing about.
     *
     * @param entityClass  the entity class
     * @param propertyName the property name
     * @return true when a declared field of that name exists somewhere in the hierarchy
     */
    public boolean isStored(Class<?> entityClass, String propertyName) {
        return findField(entityClass, propertyName) != null;
    }

    private static String examine(Class<?> entityClass, String propertyName) {
        Field field = findField(entityClass, propertyName);
        if (field == null) {
            return "no such field on " + entityClass.getSimpleName();
        }
        if (field.getType().isPrimitive()) {
            return "the property is a primitive " + field.getType().getName();
        }
        if (Collection.class.isAssignableFrom(field.getType()) || Map.class.isAssignableFrom(field.getType())) {
            // Hibernate tracks a loaded collection through its own wrapper; swapping in null throws
            // that wrapper away and with it the dirty state of the association.
            return "the property is a managed collection - clear its contents rather than the collection";
        }
        if (field.isAnnotationPresent(Id.class)) {
            return "the property is the identifier";
        }
        Column column = field.getAnnotation(Column.class);
        if (column != null && !column.nullable()) {
            return "column " + column.name() + " is NOT NULL";
        }
        JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
        if (joinColumn != null && !joinColumn.nullable()) {
            return "foreign key " + joinColumn.name() + " is NOT NULL";
        }
        Basic basic = field.getAnnotation(Basic.class);
        if (basic != null && !basic.optional()) {
            return "the mapping declares the property mandatory";
        }
        ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
        if (manyToOne != null && !manyToOne.optional()) {
            return "the association is declared mandatory";
        }
        OneToOne oneToOne = field.getAnnotation(OneToOne.class);
        if (oneToOne != null && !oneToOne.optional()) {
            return "the association is declared mandatory";
        }
        return null;
    }

    private static Field findField(Class<?> type, String name) {
        for (Class<?> current = type; current != null && current != Object.class; current = current.getSuperclass()) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                // keep walking up: entities inherit their identifier and audit columns
            }
        }
        return null;
    }
}
