package net.switchscope.security.policy;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache for {@link FieldAccess} annotation metadata.
 * Provides fast lookup of field access levels by class and field name.
 * Thread-safe and populated on demand.
 */
@Component
public class FieldAccessMetadataCache {

    private final Map<Class<?>, Map<String, FieldAccessLevel>> cache = new ConcurrentHashMap<>();

    /**
     * Gets the access level for a specific field in a DTO class.
     * Traverses the class hierarchy to find inherited annotations.
     *
     * @param dtoClass the DTO class to inspect
     * @param fieldName the field name
     * @return the configured access level, or {@link FieldAccessLevel#ADMIN_NULLABLE} if not annotated
     */
    public FieldAccessLevel getAccessLevel(Class<?> dtoClass, String fieldName) {
        Map<String, FieldAccessLevel> classMetadata = cache.computeIfAbsent(dtoClass, this::buildClassMetadata);
        return classMetadata.getOrDefault(fieldName, FieldAccessLevel.ADMIN_NULLABLE);
    }

    /**
     * Gets all field access levels for a DTO class.
     *
     * @param dtoClass the DTO class to inspect
     * @return map of field names to access levels
     */
    public Map<String, FieldAccessLevel> getClassMetadata(Class<?> dtoClass) {
        return cache.computeIfAbsent(dtoClass, this::buildClassMetadata);
    }

    /**
     * Clears the cache. Useful for testing.
     */
    public void clear() {
        cache.clear();
    }

    private Map<String, FieldAccessLevel> buildClassMetadata(Class<?> dtoClass) {
        Map<String, FieldAccessLevel> metadata = new ConcurrentHashMap<>();
        collectFieldsFromHierarchy(dtoClass, metadata);
        return metadata;
    }

    private void collectFieldsFromHierarchy(Class<?> clazz, Map<String, FieldAccessLevel> metadata) {
        if (clazz == null || clazz == Object.class) {
            return;
        }

        // Process parent class first (child annotations override parent)
        collectFieldsFromHierarchy(clazz.getSuperclass(), metadata);

        // Process declared fields in this class
        for (Field field : clazz.getDeclaredFields()) {
            FieldAccess annotation = field.getAnnotation(FieldAccess.class);
            if (annotation != null) {
                metadata.put(field.getName(), annotation.value());
            }
        }
    }
}
