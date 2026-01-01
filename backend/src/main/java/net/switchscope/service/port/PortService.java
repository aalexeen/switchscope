package net.switchscope.service.port;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.switchscope.model.port.Port;
import net.switchscope.repository.port.PortRepository;
import net.switchscope.service.CrudService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortService implements CrudService<Port> {

    private final PortRepository repository;

    @Override
    public List<Port> getAll() {
        return repository.findAll();
    }

    @Override
    public Port getById(UUID id) {
        return repository.getExisted(id);
    }

    @Override
    @Transactional
    public Port create(Port entity) {
        // TODO: implement validation
        return repository.save(entity);
    }

    @Override
    @Transactional
    public Port update(UUID id, Port entity) {
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
