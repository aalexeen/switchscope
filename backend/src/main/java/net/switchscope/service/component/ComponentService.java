package net.switchscope.service.component;

import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.switchscope.error.NotFoundException;
import net.switchscope.mapper.component.connectivity.CableRunMapper;
import net.switchscope.mapper.component.connectivity.ConnectorMapper;
import net.switchscope.mapper.component.connectivity.PatchPanelMapper;
import net.switchscope.mapper.component.device.AccessPointMapper;
import net.switchscope.mapper.component.device.NetworkSwitchMapper;
import net.switchscope.mapper.component.device.RouterMapper;
import net.switchscope.mapper.component.housing.RackMapper;
import net.switchscope.model.component.Component;
import net.switchscope.model.component.ComponentNatureEntity;
import net.switchscope.model.component.ComponentStatusEntity;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.model.component.connectivity.CableRun;
import net.switchscope.model.component.connectivity.Connector;
import net.switchscope.model.component.connectivity.PatchPanel;
import net.switchscope.model.component.device.AccessPoint;
import net.switchscope.model.component.device.NetworkSwitch;
import net.switchscope.model.component.device.Router;
import net.switchscope.model.component.housing.Rack;
import net.switchscope.repository.component.ComponentNatureRepository;
import net.switchscope.repository.component.ComponentRepository;
import net.switchscope.repository.component.ComponentStatusRepository;
import net.switchscope.repository.component.ComponentTypeRepository;
import net.switchscope.security.policy.UpdatePolicy;
import net.switchscope.security.policy.UpdatePolicyResolver;
import net.switchscope.security.policy.UpdatePolicyValidator;
import net.switchscope.service.CrudService;
import net.switchscope.to.component.ComponentTo;
import net.switchscope.to.component.connectivity.CableRunTo;
import net.switchscope.to.component.connectivity.ConnectorTo;
import net.switchscope.to.component.connectivity.PatchPanelTo;
import net.switchscope.to.component.device.AccessPointTo;
import net.switchscope.to.component.device.NetworkSwitchTo;
import net.switchscope.to.component.device.RouterTo;
import net.switchscope.to.component.housing.RackTo;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComponentService implements CrudService<Component> {

    private final ComponentRepository repository;
    private final ComponentTypeRepository componentTypeRepository;
    private final ComponentStatusRepository componentStatusRepository;
    private final ComponentNatureRepository componentNatureRepository;
    private final UpdatePolicyResolver policyResolver;
    private final UpdatePolicyValidator policyValidator;

    // Polymorphic mappers for different component types
    private final NetworkSwitchMapper networkSwitchMapper;
    private final RouterMapper routerMapper;
    private final AccessPointMapper accessPointMapper;
    private final CableRunMapper cableRunMapper;
    private final ConnectorMapper connectorMapper;
    private final PatchPanelMapper patchPanelMapper;
    private final RackMapper rackMapper;

    @Override
    public List<Component> getAll() {
        return repository.findAllWithAssociations();
    }

    /**
     * Get all components and map to DTOs within transaction.
     * This ensures lazy-loaded associations are accessible during mapping.
     */
    public List<ComponentTo> getAllAsDto() {
        List<Component> components = repository.findAllWithAssociations();
        return components.stream()
                .map(this::mapToDto)
                .filter(dto -> {
                    if (dto == null) {
                        log.warn("Filtered out null DTO from result");
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Component getById(UUID id) {
        return repository.getExisted(id);
    }

    /**
     * Get component by ID and map to DTO within transaction.
     */
    public ComponentTo getByIdAsDto(UUID id) {
        Component component = repository.findByIdWithAssociations(id)
                .orElseThrow(() -> new NotFoundException("Component with id=" + id + " not found"));
        initializeLazyAssociations(component);
        return mapToDto(component);
    }

    @Override
    @Transactional
    public Component create(Component entity) {
        // TODO: implement validation
        return repository.save(entity);
    }

    @Override
    @Transactional
    public Component update(UUID id, Component entity) {
        repository.getExisted(id);
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteExisted(id);
    }

    /**
     * Update component with role-based policy validation.
     *
     * @param id            component ID
     * @param dto           deserialized DTO
     * @param dtoClass      DTO class for policy lookup
     * @param presentFields JSON fields present in request (for null detection)
     * @param mapperFunction function to apply DTO to entity via mapper
     * @return updated component
     */
    @Transactional
    public Component updateWithPolicyValidation(
            UUID id,
            ComponentTo dto,
            Class<? extends ComponentTo> dtoClass,
            Map<String, JsonNode> presentFields,
            BiConsumer<Component, ComponentTo> mapperFunction) {

        // 1. Load existing entity with associations
        Component entity = repository.findByIdWithAssociations(id)
                .orElseThrow(() -> new NotFoundException("Component with id=" + id + " not found"));

        // 2. Validate field nullifications against policy
        UpdatePolicy policy = policyResolver.resolve();
        log.debug("Applying update policy: {} for component {}", policy.getPolicyName(), id);
        policyValidator.validate(dtoClass, presentFields, policy);

        // 3. Handle FK relationship changes
        handleFkChanges(entity, dto);

        // 4. Apply field updates via mapper
        mapperFunction.accept(entity, dto);

        // 5. Save and return
        return repository.save(entity);
    }

    /**
     * Update component with role-based policy validation and return as DTO.
     * Maps result within transaction to avoid LazyInitializationException.
     *
     * @param id            component ID
     * @param dto           deserialized DTO
     * @param dtoClass      DTO class for policy lookup
     * @param presentFields JSON fields present in request (for null detection)
     * @param mapperFunction function to apply DTO to entity via mapper
     * @return updated component as DTO
     */
    @Transactional
    public ComponentTo updateWithPolicyValidationAndReturnDto(
            UUID id,
            ComponentTo dto,
            Class<? extends ComponentTo> dtoClass,
            Map<String, JsonNode> presentFields,
            BiConsumer<Component, ComponentTo> mapperFunction) {

        Component updated = updateWithPolicyValidation(id, dto, dtoClass, presentFields, mapperFunction);
        return mapToDto(updated);
    }

    /**
     * Create component and return as DTO within transaction.
     */
    @Transactional
    public ComponentTo createAndReturnDto(Component entity) {
        Component saved = repository.save(entity);
        return mapToDto(saved);
    }

    /**
     * Initialize lazy associations for polymorphic component types.
     * This ensures all type-specific lazy fields are loaded before mapping.
     */
    private void initializeLazyAssociations(Component component) {
        if (component instanceof NetworkSwitch networkSwitch) {
            Hibernate.initialize(networkSwitch.getSwitchModel());
        } else if (component instanceof PatchPanel patchPanel) {
            Hibernate.initialize(patchPanel.getPatchPanelModel());
        } else if (component instanceof CableRun cableRun) {
            Hibernate.initialize(cableRun.getCableModel());
        } else if (component instanceof Connector connector) {
            Hibernate.initialize(connector.getConnectorModel());
        } else if (component instanceof Rack rack) {
            Hibernate.initialize(rack.getRackType());
        }
        // Router and AccessPoint don't have lazy model fields
    }

    /**
     * Map component entity to DTO.
     * Must be called within transaction context.
     */
    private ComponentTo mapToDto(Component component) {
        if (component == null) {
            log.warn("Attempting to map null component");
            return null;
        }

        try {
            initializeLazyAssociations(component);

            if (component instanceof NetworkSwitch networkSwitch) {
                return networkSwitchMapper.toTo(networkSwitch);
            } else if (component instanceof Router router) {
                return routerMapper.toTo(router);
            } else if (component instanceof AccessPoint accessPoint) {
                return accessPointMapper.toTo(accessPoint);
            } else if (component instanceof CableRun cableRun) {
                return cableRunMapper.toTo(cableRun);
            } else if (component instanceof Connector connector) {
                return connectorMapper.toTo(connector);
            } else if (component instanceof PatchPanel patchPanel) {
                return patchPanelMapper.toTo(patchPanel);
            } else if (component instanceof Rack rack) {
                return rackMapper.toTo(rack);
            } else {
                log.error("Unknown component type: {} for component id: {}", component.getClass().getName(), component.getId());
                throw new IllegalArgumentException("Unknown component type: " + component.getClass().getName());
            }
        } catch (Exception e) {
            log.error("Error mapping component id: {}, type: {}", component.getId(), component.getClass().getName(), e);
            throw e;
        }
    }

    /**
     * Handle FK relationship changes that mappers ignore.
     */
    private void handleFkChanges(Component entity, ComponentTo dto) {
        // Handle componentTypeId change
        if (dto.getComponentTypeId() != null &&
                (entity.getComponentType() == null ||
                 !Objects.equals(dto.getComponentTypeId(), entity.getComponentType().getId()))) {
            ComponentTypeEntity newType = componentTypeRepository.findById(dto.getComponentTypeId())
                    .orElseThrow(() -> new NotFoundException("Component type with id=" + dto.getComponentTypeId() + " not found"));
            entity.setComponentType(newType);
        }

        // Handle componentStatusId change
        if (dto.getComponentStatusId() != null &&
                (entity.getComponentStatus() == null ||
                 !Objects.equals(dto.getComponentStatusId(), entity.getComponentStatus().getId()))) {
            ComponentStatusEntity newStatus = componentStatusRepository.findById(dto.getComponentStatusId())
                    .orElseThrow(() -> new NotFoundException("Component status with id=" + dto.getComponentStatusId() + " not found"));
            entity.setComponentStatus(newStatus);
        }

        // Handle componentNatureId change (optional field, can be null)
        if (dto.getComponentNatureId() != null) {
            if (entity.getComponentNature() == null ||
                    !Objects.equals(dto.getComponentNatureId(), entity.getComponentNature().getId())) {
                ComponentNatureEntity newNature = componentNatureRepository.findById(dto.getComponentNatureId())
                        .orElseThrow(() -> new NotFoundException("Component nature with id=" + dto.getComponentNatureId() + " not found"));
                entity.setComponentNature(newNature);
            }
        }
    }
}
