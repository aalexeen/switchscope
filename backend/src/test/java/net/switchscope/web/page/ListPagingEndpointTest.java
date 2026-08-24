package net.switchscope.web.page;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * What a list route answers now that it can be asked for a page.
 * <p>
 * The property worth testing is not "a page comes back" - it is that <em>the page holds what the
 * list holds</em>. A paged read builds its query instead of reusing the route's JPQL, so for every
 * one of the twenty routes there is a second query that has to select the same rows as the first:
 * {@code /api/devices} unions three classes and leaves out a fourth, {@code /api/housing/racks}
 * reads one class out of a table that holds seven. Getting that wrong would not throw - it would
 * quietly return a different collection, which is why every case here compares the two answers
 * rather than inspecting one.
 * <p>
 * The routes are not only listed but discovered: {@link #theRoutesTestedHereAreTheRoutesServed}
 * asks the handler mapping which collection routes the application serves and fails if that set is
 * not exactly the list below. A twenty-first list route added without pagination therefore fails
 * this test instead of going unnoticed.
 * <p>
 * Counts are never asserted absolutely: the tests share the development database, so every
 * assertion here compares one answer against another taken in the same run.
 */
@SpringBootTest(properties = {
        "spring.testcontainers.enabled=false",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration"
})
@AutoConfigureMockMvc
class ListPagingEndpointTest {

    private static final String ADMIN = "admin@gmail.com";
    private static final String ADMIN_PASSWORD = "admin";

    /** How large a page the walking case asks for: small enough that most collections have several. */
    private static final int WALK = 5;

    /** Every route that answers with a collection. */
    private static final List<String> LIST_ROUTES = List.of(
            "/api/catalogs/component-categories",
            "/api/catalogs/component-models",
            "/api/catalogs/component-natures",
            "/api/catalogs/component-statuses",
            "/api/catalogs/component-types",
            "/api/catalogs/installable-types",
            "/api/catalogs/installation-statuses",
            "/api/catalogs/location-types",
            "/api/components",
            "/api/connectivity/cable-runs",
            "/api/connectivity/connectors",
            "/api/connectivity/patch-panels",
            "/api/devices",
            "/api/devices/access-points",
            "/api/devices/routers",
            "/api/devices/switches",
            "/api/housing/racks",
            "/api/installations",
            "/api/locations",
            "/api/ports");

    /** The GET routes that take no path variable and are still not collections. */
    private static final Set<String> NOT_COLLECTIONS = Set.of(
            "/api/auth/check", "/api/auth/profile", "/api/profile", "/api/admin/permissions/matrix");

    static Stream<String> listRoutes() {
        return LIST_ROUTES.stream();
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    @Qualifier("requestMappingHandlerMapping")
    private RequestMappingHandlerMapping handlerMapping;

    @Test
    @DisplayName("the routes tested here are exactly the collection routes the application serves")
    void theRoutesTestedHereAreTheRoutesServed() {
        Set<String> served = new TreeSet<>();
        handlerMapping.getHandlerMethods().forEach((info, handler) -> {
            if (!handler.getBeanType().getName().startsWith("net.switchscope")
                    || !info.getMethodsCondition().getMethods().contains(RequestMethod.GET)) {
                return;
            }
            for (String pattern : patterns(info)) {
                if (pattern.startsWith("/api/") && !pattern.contains("{")
                        && !NOT_COLLECTIONS.contains(pattern)) {
                    served.add(pattern);
                }
            }
        });
        assertThat(served)
                .as("a collection route that is not listed above is a route nothing here checks")
                .containsExactlyInAnyOrderElementsOf(LIST_ROUTES);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("listRoutes")
    @DisplayName("with no parameters the answer is the array it always was")
    void withoutParametersTheAnswerIsAnArray(String route) throws Exception {
        assertThat(read(route).isArray()).isTrue();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("listRoutes")
    @DisplayName("paging through a collection yields every row of the list, exactly once, and counts them")
    void aPageHoldsTheRowsTheListHolds(String route) throws Exception {
        List<String> all = ids(read(route));

        List<String> walked = new ArrayList<>();
        for (int page = 0; ; page++) {
            JsonNode chunk = read(route + "?page=" + page + "&size=" + WALK);
            if (page == 0) {
                assertThat(chunk.get("totalElements").asInt())
                        .as("the count is the database's, over the same rows the list returns")
                        .isEqualTo(all.size());
                assertThat(chunk.get("totalPages").asInt()).isEqualTo((all.size() + WALK - 1) / WALK);
                assertThat(chunk.get("page").asInt()).isZero();
                assertThat(chunk.get("size").asInt()).isEqualTo(WALK);
            }
            assertThat(chunk.get("content").size()).isLessThanOrEqualTo(WALK);
            walked.addAll(ids(chunk.get("content")));
            if (page + 1 >= chunk.get("totalPages").asInt()) {
                break;
            }
        }

        assertThat(walked)
                .as("a row seen twice, or never, means the pages are cut from a query that selects"
                        + " other rows than the list does, or orders them only partially")
                .containsExactlyInAnyOrderElementsOf(all);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("listRoutes")
    @DisplayName("a row on a page is the row the list returns, field for field")
    void aPagedRowIsTheRowTheListReturns(String route) throws Exception {
        Map<String, JsonNode> listed = new HashMap<>();
        read(route).forEach(row -> listed.put(row.get("id").asText(), row));

        JsonNode page = read(route + "?page=0&size=" + WALK);

        assertThat(page.get("content")).isNotEmpty();
        for (JsonNode row : page.get("content")) {
            assertThat(row)
                    .as("which rows come back is only half of it: a paged read maps them by its own"
                            + " path, and for eight of these routes that path runs somewhere else"
                            + " than the list's does")
                    .isEqualTo(listed.get(row.get("id").asText()));
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("listRoutes")
    @DisplayName("a sort field the row does not have is refused, not ignored")
    void refusesASortItCannotHonour(String route) throws Exception {
        mockMvc.perform(get(route + "?sort=noSuchFieldOfAnything").with(admin()))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("asking for a size alone is asking for the first page")
    void sizeAloneIsTheFirstPage() throws Exception {
        JsonNode page = read("/api/locations?size=2");

        assertThat(page.get("page").asInt()).isZero();
        assertThat(page.get("content").size()).isEqualTo(2);
    }

    @Test
    @DisplayName("a sort without a page is still the whole array, ordered")
    void aSortWithoutAPageIsStillAnArray() throws Exception {
        JsonNode ascending = read("/api/locations?sort=name:asc");
        JsonNode descending = read("/api/locations?sort=name:desc");

        assertThat(ascending.isArray()).isTrue();
        assertThat(ascending.size())
                .as("ordering is not filtering")
                .isEqualTo(read("/api/locations").size())
                .isGreaterThan(1);
        List<String> reversed = new ArrayList<>(ids(descending));
        java.util.Collections.reverse(reversed);
        assertThat(ids(ascending))
                .as("a sort the server ignored would answer the same way in both directions")
                .isEqualTo(reversed);
    }

    @Test
    @DisplayName("sorting by an association keeps the rows that have none")
    void sortingByAnAssociationKeepsRowsWithoutOne() throws Exception {
        JsonNode all = read("/api/components");
        long withoutParent = StreamSupport.stream(all.spliterator(), false)
                .filter(component -> component.get("parentComponentId").isNull())
                .count();
        assertThat(withoutParent)
                .as("without such rows this case would pass on an inner join too")
                .isPositive();

        JsonNode sorted = read("/api/components?sort=parentComponentName");

        assertThat(ids(sorted))
                .as("an inner join for the ordering would drop every parentless component")
                .containsExactlyInAnyOrderElementsOf(ids(all));
    }

    @Test
    @DisplayName("a page the API cannot serve is refused with a reason")
    void refusesAPageItCannotServe() throws Exception {
        assertRefused("/api/locations?size=0");
        assertRefused("/api/locations?size=" + (ListQuery.MAX_SIZE + 1));
        assertRefused("/api/locations?page=-1");
        assertRefused("/api/locations?sort=name:sideways");
        assertRefused("/api/locations?page=notANumber");
    }

    private void assertRefused(String url) throws Exception {
        mockMvc.perform(get(url).with(admin()))
                .andExpect(status().isUnprocessableEntity());
    }

    private JsonNode read(String url) throws Exception {
        String body = mockMvc.perform(get(url).with(admin()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body);
    }

    private static org.springframework.test.web.servlet.request.RequestPostProcessor admin() {
        return httpBasic(ADMIN, ADMIN_PASSWORD);
    }

    private static List<String> ids(JsonNode array) {
        return StreamSupport.stream(array.spliterator(), false)
                .map(row -> row.get("id").asText())
                .toList();
    }

    private static Set<String> patterns(RequestMappingInfo info) {
        Set<String> found = new TreeSet<>();
        if (info.getPathPatternsCondition() != null) {
            info.getPathPatternsCondition().getPatterns().forEach(pattern -> found.add(pattern.toString()));
        } else if (info.getPatternsCondition() != null) {
            found.addAll(info.getPatternsCondition().getPatterns());
        }
        return found;
    }
}
