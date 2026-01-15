package net.switchscope;

import net.switchscope.mapper.BaseMapper;
import net.switchscope.service.CrudService;
import net.switchscope.web.AbstractCatalogController;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("mvc-test")
@RequestMapping("/api/test-entities")
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
