package net.switchscope.service.component;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.switchscope.error.IllegalRequestDataException;
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
import net.switchscope.model.component.catalog.connectiviy.CableRunModel;
import net.switchscope.model.component.catalog.connectiviy.ConnectorModel;
import net.switchscope.model.component.catalog.connectiviy.PatchPanelModel;
import net.switchscope.model.component.catalog.device.SwitchModel;
import net.switchscope.model.component.catalog.housing.RackModelEntity;
import net.switchscope.model.component.housing.Rack;
import net.switchscope.repository.component.ComponentNatureRepository;
import net.switchscope.repository.component.ComponentRepository;
import net.switchscope.repository.component.ComponentStatusRepository;
import net.switchscope.repository.component.ComponentTypeRepository;
import net.switchscope.service.CrudService;
import net.switchscope.to.component.ComponentTo;
import net.switchscope.to.component.connectivity.CableRunTo;
import net.switchscope.to.component.connectivity.ConnectorTo;
import net.switchscope.to.component.connectivity.PatchPanelTo;
import net.switchscope.to.component.device.AccessPointTo;
import net.switchscope.to.component.device.NetworkSwitchTo;
import net.switchscope.to.component.device.RouterTo;
import net.switchscope.to.component.housing.RackTo;
import net.switchscope.web.payload.PartialUpdate;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
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
    private final ComponentReferenceResolver referenceResolver;

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

    /**
     * @deprecated entity-level create cannot resolve the DTO's foreign keys; use createFromDto(dto).
     * Kept only to satisfy {@code CrudService}.
     */
    @Override
    @Deprecated
    public Component create(Component entity) {
        throw new UnsupportedOperationException("Use createFromDto(dto)");
    }

    /**
     * @deprecated saving the detached entity built by the mapper merges nulls over every
     * association the mapper ignores; use updateFromDto(id, dto). Kept only to satisfy {@code CrudService}.
     */
    @Override
    @Deprecated
    public Component update(UUID id, Component entity) {
        throw new UnsupportedOperationException("Use updateFromDto(id, dto)");
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteExisted(id);
    }

    /**
     * Create a component from its polymorphic DTO and return it as a DTO.
     * <p>
     * The concrete type comes from the DTO's runtime class, which Jackson resolves from the
     * {@code componentClass} discriminator. Foreign keys are resolved here because the mappers
     * ignore every association - {@code component_type_id} and {@code component_status_id} are both
     * NOT NULL, so an unresolved entity cannot be inserted.
     */
    @Transactional
    public ComponentTo createFromDto(ComponentTo dto) {
        Component entity = mapToEntity(dto);
        applyReferences(entity, dto);
        return mapToDto(repository.save(entity));
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
     * Apply a polymorphic update onto the stored component and return it as a DTO.
     * <p>
     * The entity is loaded first, so associations the mapper ignores survive the update; merging a
     * detached instance would null them, starting with the NOT NULL component type and status.
     * <p>
     * One entry point for both {@code /api/components} and {@code /api/devices}: the concrete
     * mapper is chosen from the entity's runtime type here, so neither controller has to carry its
     * own copy of that dispatch. The policy check has already happened - a {@link PartialUpdate}
     * that clears anything can only come from the reader that validates first.
     */
    @Transactional
    public ComponentTo updateFromDto(UUID id, PartialUpdate<? extends ComponentTo> update) {
        ComponentTo dto = update.dto();
        Component existing = repository.findByIdWithAssociations(id)
                .orElseThrow(() -> new NotFoundException("Component with id=" + id + " not found"));
        initializeLazyAssociations(existing);
        updateFromDto(existing, dto);
        applyReferences(existing, dto);
        update.applyNulls(existing);
        return mapToDto(repository.save(existing));
    }

    /**
     * Applies the DTO onto the entity with the mapper of the matching concrete type.
     */
    private void updateFromDto(Component component, ComponentTo dto) {
        if (component instanceof NetworkSwitch entity && dto instanceof NetworkSwitchTo to) {
            networkSwitchMapper.updateFromTo(entity, to);
        } else if (component instanceof Router entity && dto instanceof RouterTo to) {
            routerMapper.updateFromTo(entity, to);
        } else if (component instanceof AccessPoint entity && dto instanceof AccessPointTo to) {
            accessPointMapper.updateFromTo(entity, to);
        } else if (component instanceof CableRun entity && dto instanceof CableRunTo to) {
            cableRunMapper.updateFromTo(entity, to);
        } else if (component instanceof Connector entity && dto instanceof ConnectorTo to) {
            connectorMapper.updateFromTo(entity, to);
        } else if (component instanceof PatchPanel entity && dto instanceof PatchPanelTo to) {
            patchPanelMapper.updateFromTo(entity, to);
        } else if (component instanceof Rack entity && dto instanceof RackTo to) {
            rackMapper.updateFromTo(entity, to);
        } else {
            throw new IllegalRequestDataException("Component type mismatch: entity="
                    + component.getClass().getSimpleName() + ", to=" + dto.getClass().getSimpleName());
        }
    }

    /**
     * Resolves every foreign key the mappers ignore: the ones shared by all components, plus the
     * catalog model link of the concrete type. An id present in the DTO replaces the current
     * reference, an absent one leaves it untouched.
     */
    private void applyReferences(Component entity, ComponentTo dto) {
        referenceResolver.applyCommonReferences(entity, dto);

        if (entity instanceof NetworkSwitch sw && dto instanceof NetworkSwitchTo swTo) {
            referenceResolver.applyModelReference(swTo.getSwitchModelId(), SwitchModel.class,
                    sw::setSwitchModel, "switchModelId");
        } else if (entity instanceof Rack rack && dto instanceof RackTo rackTo) {
            referenceResolver.applyModelReference(rackTo.getRackTypeId(), RackModelEntity.class,
                    rack::setRackType, "rackTypeId");
        } else if (entity instanceof CableRun cableRun && dto instanceof CableRunTo cableRunTo) {
            referenceResolver.applyModelReference(cableRunTo.getCableModelId(), CableRunModel.class,
                    cableRun::setCableModel, "cableModelId");
        } else if (entity instanceof Connector connector && dto instanceof ConnectorTo connectorTo) {
            referenceResolver.applyModelReference(connectorTo.getConnectorModelId(), ConnectorModel.class,
                    connector::setConnectorModel, "connectorModelId");
        } else if (entity instanceof PatchPanel patchPanel && dto instanceof PatchPanelTo patchPanelTo) {
            referenceResolver.applyModelReference(patchPanelTo.getPatchPanelModelId(), PatchPanelModel.class,
                    patchPanel::setPatchPanelModel, "patchPanelModelId");
        }
        // Router and AccessPoint have no catalog model link of their own.
    }

    /**
     * Maps a polymorphic DTO to its entity using the mapper of the DTO's concrete type.
     */
    private Component mapToEntity(ComponentTo dto) {
        if (dto instanceof NetworkSwitchTo to) {
            return networkSwitchMapper.toEntity(to);
        } else if (dto instanceof RouterTo to) {
            return routerMapper.toEntity(to);
        } else if (dto instanceof AccessPointTo to) {
            return accessPointMapper.toEntity(to);
        } else if (dto instanceof CableRunTo to) {
            return cableRunMapper.toEntity(to);
        } else if (dto instanceof ConnectorTo to) {
            return connectorMapper.toEntity(to);
        } else if (dto instanceof PatchPanelTo to) {
            return patchPanelMapper.toEntity(to);
        } else if (dto instanceof RackTo to) {
            return rackMapper.toEntity(to);
        }
        throw new IllegalRequestDataException("Unsupported component DTO: " + dto.getClass().getSimpleName());
    }
}
