package net.switchscope.service.component.connectivity;

import java.util.List;
import java.util.UUID;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.component.connectivity.PatchPanelMapper;
import net.switchscope.model.component.connectivity.CableRun;
import net.switchscope.model.component.connectivity.PatchPanel;
import net.switchscope.repository.component.connectivity.ConnectivityRepository;
import net.switchscope.model.component.catalog.connectiviy.PatchPanelModel;
import net.switchscope.service.component.ComponentReferenceResolver;
import net.switchscope.service.DtoCrudService;
import net.switchscope.web.payload.PartialUpdate;
import net.switchscope.to.component.connectivity.PatchPanelTo;
import net.switchscope.to.PageTo;
import net.switchscope.web.page.ListQuery;
import net.switchscope.web.page.PageReader;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatchPanelService implements DtoCrudService<PatchPanel, PatchPanelTo> {

    private final ConnectivityRepository repository;
    private final PatchPanelMapper mapper;
    private final ComponentReferenceResolver resolver;
    private final PageReader pageReader;

    @Override
    @SuppressWarnings("unchecked")
    public List<PatchPanel> getAll() {
        return (List<PatchPanel>) (List<?>) repository.findPatchPanels();
    }

    @Override
    public PageTo<PatchPanelTo> getPage(ListQuery query) {
        return pageReader.read(PatchPanel.class, query, patchPanel -> {
            Hibernate.initialize(patchPanel.getCableRuns());
            Hibernate.initialize(patchPanel.getPorts());
            return mapper.toTo(patchPanel);
        });
    }

    @Override
    public PatchPanel getById(UUID id) {
        PatchPanel patchPanel = repository.getExisted(id, PatchPanel.class);
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
        PatchPanel patchPanel = repository.getExisted(id, PatchPanel.class);
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
    public PatchPanelTo updateFromDto(UUID id, PartialUpdate<? extends PatchPanelTo> update) {
        PatchPanelTo dto = update.dto();
        PatchPanel existing = getById(id);
        mapper.updateFromTo(existing, dto);
        applyReferences(existing, dto);
        update.applyNulls(existing);
        return mapper.toTo(repository.save(existing));
    }

    private void applyReferences(PatchPanel entity, PatchPanelTo dto) {
        resolver.applyCommonReferences(entity, dto);
        resolver.applyModelReference(dto.getPatchPanelModelId(), PatchPanelModel.class,
                entity::setPatchPanelModel, "patchPanelModelId");
        resolver.applyCollection(dto.getCableRunIds(),
                id -> repository.findByIdAndType(id, CableRun.class).map(CableRun.class::cast),
                entity.getCableRuns(), "cableRunIds");
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteExisted(id, PatchPanel.class);
    }
}
