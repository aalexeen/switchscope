package net.switchscope.service.component.connectivity;

import java.util.List;
import java.util.UUID;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.component.connectivity.PatchPanelMapper;
import net.switchscope.model.component.connectivity.PatchPanel;
import net.switchscope.repository.component.connectivity.ConnectivityRepository;
import net.switchscope.model.component.catalog.connectiviy.PatchPanelModel;
import net.switchscope.service.component.ComponentReferenceResolver;
import net.switchscope.service.DtoCrudService;
import net.switchscope.to.component.connectivity.PatchPanelTo;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatchPanelService implements DtoCrudService<PatchPanel, PatchPanelTo> {

    private final ConnectivityRepository repository;
    private final PatchPanelMapper mapper;
    private final ComponentReferenceResolver resolver;

    @Override
    @SuppressWarnings("unchecked")
    public List<PatchPanel> getAll() {
        return (List<PatchPanel>) (List<?>) repository.findPatchPanels();
    }

    @Override
    public PatchPanel getById(UUID id) {
        PatchPanel patchPanel = (PatchPanel) repository.getExisted(id);
        Hibernate.initialize(patchPanel.getCableRuns());
        Hibernate.initialize(patchPanel.getPorts());
        return patchPanel;
    }

    /**
     * Get all patch panels and map to DTOs within transaction.
     *
     * @return list of patch panel DTOs
     */
    @SuppressWarnings("unchecked")
    public List<PatchPanelTo> getAllAsDto() {
        List<PatchPanel> patchPanels = (List<PatchPanel>) (List<?>) repository.findPatchPanels();
        // Initialize lazy collections for mapping
        patchPanels.forEach(pp -> {
            Hibernate.initialize(pp.getCableRuns());
            Hibernate.initialize(pp.getPorts());
        });
        return mapper.toToList(patchPanels);
    }

    /**
     * Get patch panel by ID and map to DTO within transaction.
     *
     * @param id patch panel ID
     * @return patch panel DTO
     */
    public PatchPanelTo getByIdAsDto(UUID id) {
        PatchPanel patchPanel = (PatchPanel) repository.getExisted(id);
        Hibernate.initialize(patchPanel.getCableRuns());
        Hibernate.initialize(patchPanel.getPorts());
        return mapper.toTo(patchPanel);
    }



    /**
     * Create a patch panel from its DTO, resolving foreign-key ids into managed references first.
     * Mapping back happens inside the transaction so lazy associations are still reachable.
     */
    @Override
    @Transactional
    public PatchPanelTo createFromDto(PatchPanelTo dto) {
        PatchPanel entity = mapper.toEntity(dto);
        applyReferences(entity, dto);
        return mapper.toTo(repository.save(entity));
    }

    /**
     * Apply the DTO onto the stored patch panel.
     * The entity is loaded first: merging the detached instance produced by the mapper would null
     * every association the mapper ignores, starting with the NOT NULL component type and status.
     */
    @Override
    @Transactional
    public PatchPanelTo updateFromDto(UUID id, PatchPanelTo dto) {
        PatchPanel existing = getById(id);
        mapper.updateFromTo(existing, dto);
        applyReferences(existing, dto);
        return mapper.toTo(repository.save(existing));
    }

    private void applyReferences(PatchPanel entity, PatchPanelTo dto) {
        resolver.applyCommonReferences(entity, dto);
        resolver.applyModelReference(dto.getPatchPanelModelId(), PatchPanelModel.class,
                entity::setPatchPanelModel, "patchPanelModelId");
    }

    /**
     * @deprecated entity-level create cannot resolve the DTO's foreign keys; use
     * {@link #createFromDto}. Kept only to satisfy {@code CrudService}.
     */
    @Override
    @Deprecated
    public PatchPanel create(PatchPanel entity) {
        throw new UnsupportedOperationException("Use createFromDto(dto)");
    }

    /**
     * @deprecated saving the detached entity built by the mapper merges nulls over every
     * association the mapper ignores; use {@link #updateFromDto}. Kept only to satisfy
     * {@code CrudService}.
     */
    @Override
    @Deprecated
    public PatchPanel update(UUID id, PatchPanel entity) {
        throw new UnsupportedOperationException("Use updateFromDto(id, dto)");
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteExisted(id);
    }
}
