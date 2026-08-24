package net.switchscope.service.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.switchscope.mapper.component.catalog.ComponentStatusMapper;
import net.switchscope.model.component.ComponentStatusEntity;
import net.switchscope.repository.component.ComponentStatusRepository;
import net.switchscope.service.DtoCrudService;
import net.switchscope.to.component.catalog.ComponentStatusTo;
import net.switchscope.web.payload.PartialUpdate;
import net.switchscope.to.PageTo;
import net.switchscope.web.page.ListQuery;
import net.switchscope.web.page.PageReader;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComponentStatusService implements DtoCrudService<ComponentStatusEntity, ComponentStatusTo> {

    private final ComponentStatusRepository repository;
    private final ComponentStatusMapper mapper;
    private final PageReader pageReader;

    @Override
    public List<ComponentStatusEntity> getAll() {
        return repository.findAllWithAssociations();
    }

    @Override
    public PageTo<ComponentStatusTo> getPage(ListQuery query) {
        return pageReader.read(ComponentStatusEntity.class, query, mapper::toTo);
    }

    @Override
    public ComponentStatusEntity getById(UUID id) {
        return repository.getExisted(id);
    }

    @Override
    @Transactional
    public ComponentStatusTo createFromDto(ComponentStatusTo dto) {
        return mapper.toTo(repository.save(mapper.toEntity(dto)));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteExisted(id);
    }

    /**
     * Mapping back happens inside the transaction, so a lazy association is still reachable.
     */
    @Override
    @Transactional
    public ComponentStatusTo updateFromDto(UUID id, PartialUpdate<? extends ComponentStatusTo> update) {
        ComponentStatusEntity existing = repository.getExisted(id);
        mapper.updateFromTo(existing, update.dto());
        update.applyNulls(existing);
        ComponentStatusEntity saved = repository.save(existing);
        return mapper.toTo(saved);
    }
}
