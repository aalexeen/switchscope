package net.switchscope.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import net.switchscope.mapper.BaseMapper;
import net.switchscope.security.permission.RequiresPermission;
import net.switchscope.service.DtoCrudService;
import net.switchscope.to.BaseTo;
import net.switchscope.web.payload.JsonPayload;
import net.switchscope.web.payload.PartialUpdate;
import net.switchscope.web.payload.PartialUpdateReader;
import net.switchscope.web.payload.PayloadValidator;

import java.util.List;
import java.util.UUID;

/**
 * Abstract controller for basic CRUD operations.
 * Provides standard REST endpoints for entities.
 * Uses DTOs for API contract while working with Entities internally.
 * Transaction management is delegated to service layer.
 * <p>
 * Writes go through {@link DtoCrudService}, which resolves the DTO's foreign-key ids and applies
 * updates onto the stored entity. The controller must not build an entity with the mapper and hand
 * it to the service: the mapper ignores associations, so saving that detached instance would null
 * the corresponding columns.
 * <p>
 * {@code update} takes the body as raw JSON rather than a bound DTO. That is not a style choice:
 * after deserialisation into a POJO a field the caller omitted and a field the caller set to
 * {@code null} are the same thing, so a controller holding only the DTO cannot honour a request to
 * clear a field - and the nine subclasses of this class serve the same objects as the polymorphic
 * controllers that can. Half the API able to clear a field and half not would be worse than
 * neither, so the reading happens here, once, for all nine.
 *
 * @param <E> the entity type
 * @param <T> the DTO (Transfer Object) type
 */
@Slf4j
public abstract class AbstractCrudController<E, T extends BaseTo> {

    /**
     * Injected on the field because the subclasses are Lombok {@code @RequiredArgsConstructor}
     * classes: giving this class a constructor parameter would mean writing an explicit
     * constructor in all nine of them to pass it up.
     */
    @Autowired
    protected PartialUpdateReader partialUpdateReader;

    @Autowired
    protected JsonPayload json;

    @Autowired
    protected PayloadValidator payloadValidator;

    protected abstract DtoCrudService<E, T> getService();

    protected abstract BaseMapper<E, T> getMapper();

    protected abstract String getEntityName();

    /**
     * The concrete DTO class, needed to bind a raw body and to look up field-access metadata.
     * Declared rather than resolved from the type parameter so that a subclass which forgets it
     * fails to compile instead of failing on its first PUT.
     *
     * @return the DTO class this controller serves
     */
    protected abstract Class<T> getDtoClass();

    @RequiresPermission("read")
    @GetMapping
    public List<T> getAll() {
        log.info("getAll {}", getEntityName());
        List<E> entities = getService().getAll();
        return getMapper().toToList(entities);
    }

    @RequiresPermission("read")
    @GetMapping("/{id}")
    public T get(@PathVariable UUID id) {
        log.info("get {} {}", getEntityName(), id);
        E entity = getService().getById(id);
        return getMapper().toTo(entity);
    }

    /**
     * Create from a raw body, for the same reason {@link #update} reads one: nine of these routes
     * serve a subtype of a polymorphic DTO, and Jackson demands the discriminator even when the URL
     * has already said which subtype it is. {@code POST /api/housing/racks} used to require
     * {@code {"componentClass": "RACK", ...}} and answer 500 without it, while the PUT next to it
     * had stopped needing it. {@code JsonPayload.bind} pins the value from the target type, so the
     * URL stays the authority on it and the body no longer has to repeat it.
     * <p>
     * Bean validation, which the typed {@code @Valid @RequestBody} gave for free, is done
     * explicitly - over the whole DTO, unlike an update, because a create carries the whole object.
     */
    @RequiresPermission("create")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public T create(@RequestBody String body) {
        T dto = json.bind(json.asObject(body), getDtoClass());
        payloadValidator.validateWhole(dto);
        log.info("create {} {}", getEntityName(), dto);
        return getService().createFromDto(dto);
    }

    /**
     * Apply a partial update. A field the body omits keeps its stored value; a field the body sends
     * as {@code null} is cleared, if the caller's policy allows it and the column can hold null.
     */
    @RequiresPermission("update")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public T update(@PathVariable UUID id, @RequestBody String jsonPayload) {
        PartialUpdate<T> update = partialUpdateReader.read(jsonPayload, getDtoClass());
        log.info("update {} {} with id={}", getEntityName(), update.dto(), id);
        return getService().updateFromDto(id, update);
    }

    @RequiresPermission("delete")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        log.info("delete {} {}", getEntityName(), id);
        getService().delete(id);
    }
}
