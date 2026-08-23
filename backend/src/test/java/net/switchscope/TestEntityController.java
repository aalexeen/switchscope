package net.switchscope;

import net.switchscope.mapper.BaseMapper;
import net.switchscope.service.CrudService;
import net.switchscope.security.permission.AuthenticatedOnly;
import net.switchscope.web.AbstractCatalogController;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("mvc-test")
@RequestMapping("/api/test-entities")
@AuthenticatedOnly(reason = "Test fixture for AbstractCatalogController, which no production controller extends and which is queued for deletion. Marked exempt rather than annotated with permissions that would then need seed rows for a class that is going away - and rather than left silent, which deny-by-default would close and which is precisely the state the model exists to make impossible.")
class TestEntityController extends AbstractCatalogController<TestEntity, TestEntityTo> {

    private final CrudService<TestEntity> service;
    private final BaseMapper<TestEntity, TestEntityTo> mapper;

    TestEntityController(CrudService<TestEntity> service, BaseMapper<TestEntity, TestEntityTo> mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    protected CrudService<TestEntity> getService() {
        return service;
    }

    @Override
    protected BaseMapper<TestEntity, TestEntityTo> getMapper() {
        return mapper;
    }

    @Override
    protected String getEntityName() {
        return "TestEntity";
    }

}
