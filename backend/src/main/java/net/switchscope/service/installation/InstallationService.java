package net.switchscope.service.installation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.switchscope.error.IllegalRequestDataException;
import net.switchscope.error.NotFoundException;
import net.switchscope.mapper.installation.InstallationMapper;
import net.switchscope.model.installation.Installation;
import net.switchscope.repository.component.ComponentRepository;
import net.switchscope.repository.installation.InstallableTypeRepository;
import net.switchscope.repository.installation.InstallationRepository;
import net.switchscope.repository.installation.InstallationStatusRepository;
import net.switchscope.repository.location.LocationRepository;
import net.switchscope.service.DtoCrudService;
import net.switchscope.web.payload.PartialUpdate;
import net.switchscope.to.installation.InstallationTo;
import net.switchscope.to.PageTo;
import net.switchscope.web.page.ListQuery;
import net.switchscope.web.page.PageReader;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InstallationService implements DtoCrudService<Installation, InstallationTo> {

    private final InstallationRepository repository;
    private final InstallationMapper mapper;
    private final LocationRepository locationRepository;
    private final InstallableTypeRepository installableTypeRepository;
    private final InstallationStatusRepository installationStatusRepository;
    private final ComponentRepository componentRepository;
    private final PageReader pageReader;

    @Override
    public List<Installation> getAll() {
        List<Installation> installations = repository.findAllWithRelationships();
        // Initialize required associations while the transactional session is open
        installations.forEach(this::initializeForMapping);
        return installations;
    }

    @Override
    public PageTo<InstallationTo> getPage(ListQuery query) {
        return pageReader.read(Installation.class, query, installation -> {
            initializeForMapping(installation);
            return mapper.toTo(installation);
        });
    }

    @Override
    public Installation getById(UUID id) {
        Installation installation = repository.findByIdWithRelationships(id)
                .orElseThrow(() -> new NotFoundException("Installation with id=" + id + " not found"));
        initializeForMapping(installation);
        return installation;
    }

    /**
     * Create an installation from its DTO, resolving foreign-key ids into managed references first.
     * All four of location, installable type, status and installed item are NOT NULL in the schema,
     * and the mapper ignores every one of them, so without this step the insert cannot succeed.
     */
    @Override
    @Transactional
    public InstallationTo createFromDto(InstallationTo dto) {
        Installation entity = mapper.toEntity(dto);
        applyReferences(entity, dto);
        Installation saved = repository.save(entity);
        initializeForMapping(saved);
        return mapper.toTo(saved);
    }

    /**
     * Apply the DTO onto the stored installation.
     * The entity is loaded first: merging the detached instance produced by the mapper would null
     * the location, installable type and status columns.
     */
    @Override
    @Transactional
    public InstallationTo updateFromDto(UUID id, PartialUpdate<? extends InstallationTo> update) {
        InstallationTo dto = update.dto();
        Installation existing = repository.findByIdWithRelationships(id)
                .orElseThrow(() -> new NotFoundException("Installation with id=" + id + " not found"));
        mapper.updateFromTo(existing, dto);
        applyReferences(existing, dto);
        update.applyNulls(existing);
        Installation saved = repository.save(existing);
        initializeForMapping(saved);
        return mapper.toTo(saved);
    }

    /**
     * Resolves the DTO's foreign-key ids. An id present in the DTO replaces the current reference,
     * an absent one leaves it untouched, so a partial update does not clear associations the caller
     * did not mention. The mandatory references are checked afterwards, turning an incomplete create
     * into a 422 rather than a constraint violation.
     */
    private void applyReferences(Installation entity, InstallationTo dto) {
        if (dto.getLocationId() != null) {
            entity.setLocation(locationRepository.findById(dto.getLocationId())
                    .orElseThrow(() -> new NotFoundException(
                            "Location with id=" + dto.getLocationId() + " not found")));
        }
        if (dto.getInstalledItemTypeId() != null) {
            entity.setInstalledItemType(installableTypeRepository.findById(dto.getInstalledItemTypeId())
                    .orElseThrow(() -> new NotFoundException(
                            "Installable type with id=" + dto.getInstalledItemTypeId() + " not found")));
        }
        if (dto.getStatusId() != null) {
            entity.setStatus(installationStatusRepository.findById(dto.getStatusId())
                    .orElseThrow(() -> new NotFoundException(
                            "Installation status with id=" + dto.getStatusId() + " not found")));
        }
        if (dto.getComponentId() != null) {
            entity.setComponent(componentRepository.findById(dto.getComponentId())
                    .orElseThrow(() -> new NotFoundException(
                            "Housing component with id=" + dto.getComponentId() + " not found")));
        }
        if (dto.getInstalledItemId() != null) {
            entity.setInstalledItemId(dto.getInstalledItemId());
        }

        if (entity.getLocation() == null) {
            throw new IllegalRequestDataException("locationId is required");
        }
        if (entity.getInstalledItemType() == null) {
            throw new IllegalRequestDataException("installedItemTypeId is required");
        }
        if (entity.getStatus() == null) {
            throw new IllegalRequestDataException("statusId is required");
        }
        if (entity.getInstalledItemId() == null) {
            throw new IllegalRequestDataException("installedItemId is required");
        }
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteExisted(id);
    }

    /**
     * Touches lazily-loaded associations that are needed by mappers to avoid
     * LazyInitializationException after the transaction closes.
     */
    private void initializeForMapping(Installation installation) {
        if (installation == null) {
            return;
        }
        if (installation.getLocation() != null) {
            installation.getLocation().getFullPath();
        }
        if (installation.getComponent() != null) {
            installation.getComponent().getName();
        }
        if (installation.getInstalledItemType() != null) {
            installation.getInstalledItemType().getCode();
        }
        if (installation.getStatus() != null) {
            installation.getStatus().getCode();
        }
    }
}
