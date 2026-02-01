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
import net.switchscope.service.CrudService;
import net.switchscope.to.component.connectivity.PatchPanelTo;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatchPanelService implements CrudService<PatchPanel> {

    private final ConnectivityRepository repository;
    private final PatchPanelMapper mapper;

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
     * Create patch panel and return as DTO within transaction.
     *
     * @param entity patch panel entity to create
     * @return created patch panel as DTO
     */
    @Transactional
    public PatchPanelTo createAndReturnDto(PatchPanel entity) {
        PatchPanel saved = repository.save(entity);
        return mapper.toTo(saved);
    }

    /**
     * Update patch panel and return as DTO within transaction.
     *
     * @param id patch panel ID
     * @param entity patch panel entity with updates
     * @return updated patch panel as DTO
     */
    @Transactional
    public PatchPanelTo updateAndReturnDto(UUID id, PatchPanel entity) {
        repository.getExisted(id);
        entity.setId(id);
        PatchPanel saved = repository.save(entity);
        return mapper.toTo(saved);
    }

    @Override
    @Transactional
    public PatchPanel create(PatchPanel entity) {
        // TODO: implement validation
        return repository.save(entity);
    }

    @Override
    @Transactional
    public PatchPanel update(UUID id, PatchPanel entity) {
        repository.getExisted(id);
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteExisted(id);
    }
}
