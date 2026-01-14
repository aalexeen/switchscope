package net.switchscope.service.component.connectivity;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.switchscope.model.component.connectivity.PatchPanel;
import net.switchscope.repository.component.connectivity.ConnectivityRepository;
import net.switchscope.service.CrudService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatchPanelService implements CrudService<PatchPanel> {

    private final ConnectivityRepository repository;

    @Override
    @SuppressWarnings("unchecked")
    public List<PatchPanel> getAll() {
        return (List<PatchPanel>) (List<?>) repository.findPatchPanels();
    }

    @Override
    public PatchPanel getById(UUID id) {
        return (PatchPanel) repository.getExisted(id);
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
