package net.switchscope.web.page;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Which rows a list route answers with, once the caller can say.
 * <p>
 * A filter is a parameter this API does not declare: the fields a route accepts are the fields its
 * row has, resolved through the same rule that resolves a sort field. Two things therefore have to
 * be true of every filter, and both are checked here rather than argued: the rows that come back
 * are the rows that match, and a filter the server cannot honour is refused by name. The second is
 * the one worth a test - a filter quietly dropped answers with rows the caller excluded and says
 * nothing about it, which looks exactly like a working feature.
 * <p>
 * Two catalog rows are created for each case and removed afterwards, so the assertions do not rest
 * on what the shared development database happens to hold; where seeded data is used
 * ({@code /api/components}), the case compares one answer against another rather than against a
 * number.
 */
@SpringBootTest(properties = {
        "spring.testcontainers.enabled=false",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration"
})
@AutoConfigureMockMvc
class ListFilteringEndpointTest {

    private static final String CATEGORIES = "/api/catalogs/component-categories";
    private static final String COMPONENTS = "/api/components";

    /** In the description of one row only, so a search that names its fields can be told apart. */
    private static final String IN_DESCRIPTION = "kettledrum";

    /** In the name of the other row, and a LIKE wildcard, so escaping is provable. */
    private static final String WITH_WILDCARD = "50% humidity";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID described;
    private UUID wildcarded;
    private String describedName;
    private String wildcardedName;

    @BeforeEach
    void createTwoRows() throws Exception {
        describedName = "filter-described-" + UUID.randomUUID();
        wildcardedName = "filter-" + WITH_WILDCARD + "-" + UUID.randomUUID();
        described = create(describedName, "a description mentioning a " + IN_DESCRIPTION);
        wildcarded = create(wildcardedName, "no such word here");
    }

    @AfterEach
    void removeThem() throws Exception {
        for (UUID id : List.of(described, wildcarded)) {
            mockMvc.perform(delete(CATEGORIES + "/" + id).with(admin()));
        }
    }

    @Test
    @DisplayName("a filter on a value of the row answers with the rows that hold it")
    void filtersByAValueOfTheRow() throws Exception {
        JsonNode filtered = read(CATEGORIES, "name", describedName);

        assertThat(ids(filtered)).containsExactly(described.toString());
    }

    @Test
    @DisplayName("a filter on a value one association away answers the same way")
    void filtersByAValueOneAssociationAway() throws Exception {
        JsonNode all = read(COMPONENTS);
        JsonNode racks = read(COMPONENTS, "componentTypeCode", "RACK");

        assertThat(racks).isNotEmpty();
        assertThat(racks.size())
                .as("a filter that answers with everything has not filtered")
                .isLessThan(all.size());
        assertThat(StreamSupport.stream(racks.spliterator(), false))
                .allMatch(row -> "RACK".equals(row.get("componentTypeCode").asText()));
    }

    @Test
    @DisplayName("repeating a filter asks for any of its values")
    void repeatingAFilterAsksForAnyOfThem() throws Exception {
        List<String> racks = ids(read(COMPONENTS, "componentTypeCode", "RACK"));
        List<String> routers = ids(read(COMPONENTS, "componentTypeCode", "ROUTER"));

        JsonNode either = read(COMPONENTS, "componentTypeCode", "RACK", "componentTypeCode", "ROUTER");

        assertThat(ids(either))
                .containsExactlyInAnyOrderElementsOf(
                        java.util.stream.Stream.concat(racks.stream(), routers.stream()).toList());
    }

    @Test
    @DisplayName("a filtered page counts the filtered rows, not the table")
    void aFilteredPageCountsWhatItFiltered() throws Exception {
        int racks = read(COMPONENTS, "componentTypeCode", "RACK").size();
        int all = read(COMPONENTS).size();

        JsonNode page = read(COMPONENTS, "componentTypeCode", "RACK", "page", "0", "size", "1");

        assertThat(page.get("totalElements").asInt())
                .as("counting the whole table would send a client paging through pages of nothing")
                .isEqualTo(racks)
                .isNotEqualTo(all);
        assertThat(page.get("content").size()).isEqualTo(1);
    }

    @Test
    @DisplayName("a filter with no value filters nothing")
    void anEmptyValueFiltersNothing() throws Exception {
        assertThat(ids(read(CATEGORIES, "name", "")))
                .as("a cleared box in a form sends the parameter without a value, and means all")
                .containsExactlyInAnyOrderElementsOf(ids(read(CATEGORIES)));
    }

    @Test
    @DisplayName("a search finds the text in any of the row's text, whatever the case")
    void searchFindsTextAnywhere() throws Exception {
        assertThat(ids(read(CATEGORIES, "search", IN_DESCRIPTION)))
                .as("the word is in the description of one row and nowhere else")
                .containsExactly(described.toString());
        assertThat(ids(read(CATEGORIES, "search", IN_DESCRIPTION.toUpperCase())))
                .containsExactly(described.toString());
    }

    @Test
    @DisplayName("a search asked for particular fields looks in those and no others")
    void searchLooksWhereItIsTold() throws Exception {
        assertThat(ids(read(CATEGORIES, "search", IN_DESCRIPTION, "searchIn", "description")))
                .containsExactly(described.toString());
        assertThat(ids(read(CATEGORIES, "search", IN_DESCRIPTION, "searchIn", "name")))
                .as("the word is not in any name, so naming the field has to change the answer")
                .isEmpty();
    }

    @Test
    @DisplayName("a wildcard in the searched-for text is text, not a wildcard")
    void aWildcardIsSearchedForLiterally() throws Exception {
        List<String> found = ids(read(CATEGORIES, "search", "%"));

        assertThat(found)
                .as("an unescaped per-cent sign in a LIKE pattern matches every row there is")
                .contains(wildcarded.toString())
                .doesNotContain(described.toString());
    }

    @Test
    @DisplayName("a filter on a field the row does not have is refused by name")
    void refusesAFilterItCannotHonour() throws Exception {
        mockMvc.perform(request(CATEGORIES, "noSuchFieldOfAnything", "1"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("a value the column cannot hold is refused, with the type named")
    void refusesAValueTheColumnCannotHold() throws Exception {
        assertRefused(COMPONENTS, "UUID", "componentTypeId", "notauuid");
        assertRefused("/api/ports", "Boolean", "poeEnabled", "yes");
    }

    @Test
    @DisplayName("searching in something that is not text is refused")
    void refusesToSearchInWhatIsNotText() throws Exception {
        assertRefused("/api/ports", "Boolean", "search", "x", "searchIn", "poeEnabled");
    }

    private void assertRefused(String url, String mentioning, String... parameters) throws Exception {
        String body = mockMvc.perform(request(url, parameters))
                .andExpect(status().isUnprocessableEntity())
                .andReturn().getResponse().getContentAsString();
        assertThat(objectMapper.readTree(body).get("detail").asText())
                .as("a refusal that does not say which parameter was wrong sends the caller to the source")
                .contains(mentioning);
    }

    private UUID create(String name, String description) throws Exception {
        JsonNode seeded = objectMapper.readTree(body(CATEGORIES));
        assertThat(seeded).as("the seeded catalog is the shape this test copies").isNotEmpty();
        ObjectNode row = ((ObjectNode) seeded.get(0)).deepCopy();
        row.remove("id");
        row.remove("createdAt");
        row.remove("updatedAt");
        row.put("name", name);
        row.put("description", description);
        if (row.has("code")) {
            row.put("code", ("FILTER_" + UUID.randomUUID()).toUpperCase().replace('-', '_'));
        }
        String created = mockMvc.perform(post(CATEGORIES).with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(row)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return UUID.fromString(objectMapper.readTree(created).get("id").asText());
    }

    private JsonNode read(String url, String... parameters) throws Exception {
        return objectMapper.readTree(body(url, parameters));
    }

    private String body(String url, String... parameters) throws Exception {
        return mockMvc.perform(request(url, parameters))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * Parameters are set rather than written into the URL: a query string given to MockMvc is
     * encoded again on its way in, so {@code ?search=%25} arrives as the text "%25" and a case
     * about a literal per-cent sign would pass while testing nothing. Written as name and value
     * they reach the resolver exactly as an HTTP client would send them. Repeating a name is
     * repeating the parameter.
     */
    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder request(
            String url, String... parameters) {
        org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder builder =
                get(url).with(admin()).accept(MediaType.APPLICATION_JSON);
        for (int i = 0; i < parameters.length; i += 2) {
            builder = builder.param(parameters[i], parameters[i + 1]);
        }
        return builder;
    }

    private static List<String> ids(JsonNode rows) {
        JsonNode array = rows.isArray() ? rows : rows.get("content");
        return StreamSupport.stream(array.spliterator(), false)
                .map(row -> row.get("id").asText())
                .toList();
    }

    private static RequestPostProcessor admin() {
        return httpBasic("admin@gmail.com", "admin");
    }
}
