package net.switchscope.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import net.switchscope.AbstractContextTest;
import net.switchscope.error.DataConflictException;
import net.switchscope.model.component.ComponentStatusEntity;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.model.component.housing.Rack;
import net.switchscope.repository.component.ComponentRepository;
import net.switchscope.repository.component.ComponentStatusRepository;
import net.switchscope.repository.component.ComponentTypeRepository;
import net.switchscope.repository.component.housing.HousingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * What happens to the children when their parent is deleted.
 * <p>
 * Three answers were in the tree at once. The JPA mappings said cascade: {@code childLocations} and
 * {@code childComponents} carried {@code cascade = ALL, orphanRemoval = true}. The schema said
 * detach: both self-referencing foreign keys were {@code ON DELETE SET NULL}. The code did neither
 * on purpose - {@code deleteExisted} is a bulk JPQL statement that never loads the row, so no
 * cascade ever ran and the database's answer was the one that took effect: deleting a building
 * turned its floors into root locations, silently, and deleting a rack turned the components
 * mounted in it into free-standing ones.
 * <p>
 * The answer is now one: a parent with children is not deleted at all. Nothing is destroyed
 * without being asked for, and nothing is left hanging.
 */
@SpringBootTest(properties = {
        "spring.testcontainers.enabled=false",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration"
})
@AutoConfigureMockMvc
class HierarchyDeleteTest extends AbstractContextTest {

    private static final String LOCATIONS = "/api/locations";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HousingRepository housingRepository;

    @Autowired
    private ComponentRepository componentRepository;

    @Autowired
    private ComponentTypeRepository componentTypeRepository;

    @Autowired
    private ComponentStatusRepository componentStatusRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("a location with children is not deleted, and its children stay attached to it")
    void refusesToDeleteALocationWithChildren() throws Exception {
        UUID parent = createLocation("parent", null);
        UUID child = createLocation("child", parent);
        try {
            mockMvc.perform(delete(LOCATIONS + "/" + parent).with(httpBasic("admin@gmail.com", "admin")))
                    .andExpect(status().isConflict());

            assertThat(read(LOCATIONS, parent))
                    .as("a refused delete must leave the row it refused to delete")
                    .isNotNull();
            assertThat(read(LOCATIONS, child).get("parentLocationId").asText())
                    .as("this is the half the foreign key used to decide on its own: the child was"
                            + " kept, but as a root location, and nobody had asked for that")
                    .isEqualTo(parent.toString());
        } finally {
            remove(LOCATIONS, child);
            remove(LOCATIONS, parent);
        }
    }

    @Test
    @DisplayName("a location with no children still deletes")
    void deletesALeafLocation() throws Exception {
        UUID leaf = createLocation("leaf", null);

        mockMvc.perform(delete(LOCATIONS + "/" + leaf).with(httpBasic("admin@gmail.com", "admin")))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(LOCATIONS + "/" + leaf).with(httpBasic("admin@gmail.com", "admin")))
                .andExpect(status().isNotFound());
    }

    /**
     * The component half goes through the repository rather than through {@code /api/housing/racks}:
     * the seven component mappers are {@code @MockBean} in {@link AbstractContextTest}, so a rack
     * created over HTTP in a test comes back from the mapper as null. Inside a rolled-back
     * transaction, and with the persistence context cleared before each assertion - a bulk delete
     * leaves managed instances untouched, so a cached child would report a parent it no longer has.
     */
    @Test
    @Transactional
    @DisplayName("a component with children is not deleted, and its children stay mounted in it")
    void refusesToDeleteAComponentWithChildren() {
        Rack parent = housingRepository.saveAndFlush(newRack("hierarchy-delete-parent"));
        Rack child = newRack("hierarchy-delete-child");
        child.setParentComponent(parent);
        housingRepository.saveAndFlush(child);
        entityManager.clear();

        assertThatThrownBy(() -> housingRepository.deleteExisted(parent.getId(), Rack.class))
                .as("the leaf-type route deletes by a bulk statement that no cascade can reach, so"
                        + " the refusal has to come before it")
                .isInstanceOf(DataConflictException.class);

        entityManager.clear();
        assertThat(componentRepository.findById(parent.getId()))
                .as("a refused delete must leave the row it refused to delete")
                .isPresent();
        assertThat(componentRepository.findById(child.getId()).orElseThrow().getParentComponent())
                .as("this is what the foreign key used to decide on its own: the child survived,"
                        + " mounted in nothing, and nobody had asked for that")
                .isNotNull();
    }

    @Test
    @Transactional
    @DisplayName("a component with no children still deletes")
    void deletesALeafComponent() {
        Rack leaf = housingRepository.saveAndFlush(newRack("hierarchy-delete-leaf"));
        entityManager.clear();

        housingRepository.deleteExisted(leaf.getId(), Rack.class);

        entityManager.clear();
        assertThat(componentRepository.findById(leaf.getId())).isEmpty();
    }

    private Rack newRack(String name) {
        ComponentTypeEntity type = componentTypeRepository.findAll().getFirst();
        ComponentStatusEntity status = componentStatusRepository.findAll().getFirst();
        Rack rack = new Rack();
        rack.setName(name);
        rack.setManufacturer("test");
        rack.setSerialNumber(name + "-" + UUID.randomUUID());
        rack.setComponentType(type);
        rack.setComponentStatus(status);
        return rack;
    }

    private UUID createLocation(String name, UUID parentId) throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name", name + "-" + UUID.randomUUID());
        body.put("typeId", firstId("/api/catalogs/location-types"));
        if (parentId != null) {
            body.put("parentLocationId", parentId.toString());
        }
        return created(LOCATIONS, body);
    }

    private UUID created(String url, Map<String, Object> body) throws Exception {
        String response = mockMvc.perform(post(url)
                        .with(httpBasic("admin@gmail.com", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return UUID.fromString(objectMapper.readTree(response).get("id").asText());
    }

    private String firstId(String catalogUrl) throws Exception {
        JsonNode all = objectMapper.readTree(getBody(catalogUrl));
        assertThat(all).as("the seeded catalog is the fixture this test stands on").isNotEmpty();
        return all.get(0).get("id").asText();
    }

    private JsonNode read(String url, UUID id) throws Exception {
        return objectMapper.readTree(mockMvc.perform(get(url + "/" + id)
                        .with(httpBasic("admin@gmail.com", "admin"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());
    }

    private String getBody(String url) throws Exception {
        return mockMvc.perform(get(url)
                        .with(httpBasic("admin@gmail.com", "admin"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    private void remove(String url, UUID id) throws Exception {
        mockMvc.perform(delete(url + "/" + id).with(httpBasic("admin@gmail.com", "admin")));
    }
}
