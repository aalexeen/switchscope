package net.switchscope.service.component.device;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.switchscope.model.component.device.Router;
import net.switchscope.repository.component.device.DeviceRepository;
import net.switchscope.service.CrudService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RouterService implements CrudService<Router> {

    private final DeviceRepository repository;

    @Override
    @SuppressWarnings("unchecked")
    public List<Router> getAll() {
        return (List<Router>) (List<?>) repository.findRouters();
    }

    @Override
    public Router getById(UUID id) {
        return (Router) repository.getExisted(id);
    }

    @Override
    @Transactional
    public Router create(Router entity) {
        // TODO: implement validation
        return repository.save(entity);
    }

    @Override
    @Transactional
    public Router update(UUID id, Router entity) {
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
