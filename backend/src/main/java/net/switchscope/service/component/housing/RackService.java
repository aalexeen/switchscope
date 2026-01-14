package net.switchscope.service.component.housing;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.switchscope.model.component.housing.Rack;
import net.switchscope.repository.component.housing.HousingRepository;
import net.switchscope.service.CrudService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RackService implements CrudService<Rack> {

    private final HousingRepository repository;

    @Override
    @SuppressWarnings("unchecked")
    public List<Rack> getAll() {
        return (List<Rack>) (List<?>) repository.findRacks();
    }

    @Override
    public Rack getById(UUID id) {
        return (Rack) repository.getExisted(id);
    }

    @Override
    @Transactional
    public Rack create(Rack entity) {
        // TODO: implement validation
        return repository.save(entity);
    }

    @Override
    @Transactional
    public Rack update(UUID id, Rack entity) {
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
