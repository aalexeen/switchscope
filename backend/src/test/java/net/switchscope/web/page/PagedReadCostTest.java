package net.switchscope.web.page;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.EntityType;
import net.switchscope.model.component.Component;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * What a page costs to read, expressed as something that can fail.
 * <p>
 * The first paged implementation was correct and unusable: {@code GET /api/components?page=0&size=10}
 * took <b>27 seconds</b> where the whole unpaged list took half of one. Nothing was wrong with the
 * count of queries - 48 against the list's 62 - so a test counting statements would have passed.
 * One query was pathological: the page came back holding proxies, and initialising a proxy goes
 * through Hibernate's load-by-id, which puts every eagerly-mapped association of a component into a
 * single select - seven joined collections at once, a Cartesian product, thirteen seconds each.
 * <p>
 * The property that broke, then, is not "few queries" but "no component is ever loaded one at a
 * time": a page fetches what its rows point at in the query that reads them, exactly as the unpaged
 * finders do. Hibernate counts such loads per entity, and for every class of component the count
 * has to be zero. It is not zero for everything - a page of ten costs ten lazy loads of catalog
 * rows and of the locations an installation's path runs through, and those are ordinary loads by
 * id of small rows. The component is the one whose load-by-id is not ordinary.
 */
@SpringBootTest(properties = {
        "spring.testcontainers.enabled=false",
        "spring.jpa.properties.hibernate.generate_statistics=true",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration"
})
@AutoConfigureMockMvc
class PagedReadCostTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    @DisplayName("a page of components is read without initialising a single lazy proxy")
    void aPageFetchesWhatItNeedsInTheQueryThatReadsIt() throws Exception {
        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.clear();

        mockMvc.perform(get("/api/components?page=0&size=10").with(httpBasic("admin@gmail.com", "admin")))
                .andExpect(status().isOk());

        long loadedOneAtATime = entityManagerFactory.getMetamodel().getEntities().stream()
                .map(EntityType::getJavaType)
                .filter(Component.class::isAssignableFrom)
                .mapToLong(type -> statistics.getEntityStatistics(type.getName()).getFetchCount())
                .sum();

        assertThat(loadedOneAtATime)
                .as("a component fetched on its own is a component fetched by the query that joins"
                        + " seven of its catalog's collections at once")
                .isZero();
    }
}
