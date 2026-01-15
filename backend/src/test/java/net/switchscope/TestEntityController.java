package net.switchscope;

import net.switchscope.mapper.BaseMapper;
import net.switchscope.service.CrudService;
import net.switchscope.to.BaseTo;
import net.switchscope.web.AbstractCatalogController;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Profile("mvc-test")
@RequestMapping("/api/test-entities")
class TestEntityController extends AbstractCatalogController<TestEntity, TestEntityController.TestEntityTo> {

    private final CrudService<TestEntity> service;

    TestEntityController(CrudService<TestEntity> service) {
        this.service = service;
    }

    @Override
    protected CrudService<TestEntity> getService() {
        return service;
    }

    @Override
    protected BaseMapper<TestEntity, TestEntityTo> getMapper() {
        return new TestEntityMapper();
    }

    @Override
    protected String getEntityName() {
        return "TestEntity";
    }

    // Simple DTO used by tests
    static class TestEntityTo extends BaseTo {
        private String name;

        public TestEntityTo() {}
        public TestEntityTo(UUID id, String name) {
            this.setId(id);
            this.name = name;
        }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    // Minimal mapper for tests
    static class TestEntityMapper implements BaseMapper<TestEntity, TestEntityTo> {
        @Override
        public TestEntityTo toTo(TestEntity entity) {
            if (entity == null) return null;
            return new TestEntityTo(entity.getId(), entity.getName());
        }

        @Override
        public List<TestEntityTo> toToList(java.util.Collection<TestEntity> entities) {
            java.util.ArrayList<TestEntityTo> list = new java.util.ArrayList<>();
            for (TestEntity e : entities) {
                list.add(toTo(e));
            }
            return list;
        }

        @Override
        public TestEntity toEntity(TestEntityTo dto) {
            if (dto == null) return null;
            return new TestEntity(dto.getId(), dto.getName());
        }

        @Override
        public java.util.List<TestEntity> toEntityList(java.util.Collection<TestEntityTo> tos) {
            java.util.ArrayList<TestEntity> list = new java.util.ArrayList<>();
            for (TestEntityTo dto : tos) {
                list.add(toEntity(dto));
            }
            return list;
        }

        @Override
        public TestEntity updateFromTo(TestEntity entity, TestEntityTo to) {
            if (entity == null) entity = new TestEntity();
            entity.setId(to.getId());
            entity.setName(to.getName());
            return entity;
        }
    }
}
