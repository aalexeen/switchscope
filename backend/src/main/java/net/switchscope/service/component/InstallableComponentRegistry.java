package net.switchscope.service.component;

import jakarta.annotation.PostConstruct;
import net.switchscope.model.component.InstallableCategory;
import net.switchscope.model.component.InstallableComponent;
import net.switchscope.repository.installation.InstallableTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class InstallableComponentRegistry {

    private static final Logger log = LoggerFactory.getLogger(InstallableComponentRegistry.class);
    private static final String BASE_PACKAGE = "net.switchscope.model.component";

    private final InstallableTypeRepository repository;
    private final Map<String, Class<?>> classByCode = new HashMap<>();
    private final Map<String, InstallableCategory> categoryByCode = new HashMap<>();
    private final Set<String> unimplementedCodes = new HashSet<>();

    public InstallableComponentRegistry(InstallableTypeRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init() {
        scanAnnotatedComponents();
        validateAgainstDatabase();
    }

    public boolean isDeviceType(String code) {
        return hasCategory(code, InstallableCategory.DEVICE);
    }

    public boolean isConnectivityType(String code) {
        return hasCategory(code, InstallableCategory.CONNECTIVITY);
    }

    public boolean isHousingType(String code) {
        return hasCategory(code, InstallableCategory.HOUSING);
    }

    public boolean isPowerType(String code) {
        return hasCategory(code, InstallableCategory.POWER);
    }

    public Optional<Class<?>> getClassByCode(String code) {
        return Optional.ofNullable(classByCode.get(code));
    }

    public Optional<InstallableCategory> getCategoryByCode(String code) {
        return Optional.ofNullable(categoryByCode.get(code));
    }

    public boolean isImplemented(String code) {
        return code != null && classByCode.containsKey(code);
    }

    private boolean hasCategory(String code, InstallableCategory category) {
        if (code == null) {
            return false;
        }
        return category.equals(categoryByCode.get(code));
    }

    private void scanAnnotatedComponents() {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(InstallableComponent.class));

        scanner.findCandidateComponents(BASE_PACKAGE).forEach(definition -> {
            String className = definition.getBeanClassName();
            if (className == null) {
                return;
            }
            try {
                Class<?> componentClass = Class.forName(className);
                InstallableComponent annotation = componentClass.getAnnotation(InstallableComponent.class);
                if (annotation != null) {
                    registerComponent(annotation.code(), componentClass, annotation.category());
                }
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Failed to load installable component class: " + className, e);
            }
        });
    }

    private void registerComponent(String code, Class<?> componentClass, InstallableCategory category) {
        Class<?> existingClass = classByCode.get(code);
        if (existingClass != null) {
            throw new IllegalStateException(
                    "Duplicate installable component code '" + code + "' for " +
                            existingClass.getName() + " and " + componentClass.getName());
        }
        classByCode.put(code, componentClass);
        categoryByCode.put(code, category);
    }

    public void validateAgainstDatabase() {
        List<String> dbCodes = repository.findAllCodes();
        if (dbCodes == null) {
            dbCodes = Collections.emptyList();
        }

        for (String code : dbCodes) {
            if (!classByCode.containsKey(code)) {
                unimplementedCodes.add(code);
                log.warn("Installable type code '{}' exists in database but has no Java class", code);
            }
        }

        for (String code : classByCode.keySet()) {
            if (!dbCodes.contains(code)) {
                log.warn("Installable component code '{}' exists in Java but is missing in database", code);
            }
        }
    }
}
