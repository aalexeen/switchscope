package net.switchscope;

import net.switchscope.service.CrudService;
import net.switchscope.web.AbstractCatalogController;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Profile("mvc-test")
@RequestMapping("/api/test-entities")
class TestEntityController extends AbstractCatalogController<TestEntity> {

    private final CrudService<TestEntity> service;

    TestEntityController(CrudService<TestEntity> service) {
        this.service = service;
    }

    @Override
    protected CrudService<TestEntity> getService() {
        return service;
    }

    @Override
    protected String getEntityName() {
        return "TestEntity";
    }

}
