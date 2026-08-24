package net.switchscope.service.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.switchscope.mapper.component.catalog.ComponentNatureMapper;
import net.switchscope.model.component.ComponentNatureEntity;
import net.switchscope.repository.component.ComponentNatureRepository;
import net.switchscope.service.DtoCrudService;
import net.switchscope.to.component.catalog.ComponentNatureTo;
import net.switchscope.web.payload.PartialUpdate;
import net.switchscope.to.PageTo;
import net.switchscope.web.page.ListQuery;
import net.switchscope.web.page.PageReader;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComponentNatureService implements DtoCrudService<ComponentNatureEntity, ComponentNatureTo> {

    private final ComponentNatureRepository repository;
    private final ComponentNatureMapper mapper;
    private final PageReader pageReader;

    @Override
    public List<ComponentNatureEntity> getAll() {
        return repository.findAllWithAssociations();
    }

    @Override
    public PageTo<ComponentNatureTo> getPage(ListQuery query) {
        return pageReader.read(ComponentNatureEntity.class, query, mapper::toTo);
    }

    @Override
    public ComponentNatureEntity getById(UUID id) {
        return repository.getExisted(id);
    }

    @Override
    @Transactional
    public ComponentNatureTo createFromDto(ComponentNatureTo dto) {
        return mapper.toTo(repository.save(mapper.toEntity(dto)));
    }

    @Override
    @Transactional
    public ComponentNatureTo updateFromDto(UUID id, PartialUpdate<? extends ComponentNatureTo> update) {
        ComponentNatureEntity existing = repository.getExisted(id);
        mapper.updateFromTo(existing, update.dto());
        update.applyNulls(existing);
        return mapper.toTo(repository.save(existing));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteExisted(id);
    }
}
