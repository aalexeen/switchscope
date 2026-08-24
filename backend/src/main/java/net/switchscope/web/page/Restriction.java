package net.switchscope.web.page;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Which rows of a table a list route is about, when that is narrower than "all of them".
 * <p>
 * Several routes are served by a repository whose domain type is the root of a single-table
 * hierarchy: {@code /api/devices} spans three classes of the components table and deliberately
 * leaves out a fourth. The unpaged queries say so in JPQL ({@code SELECT d FROM NetworkSwitch d});
 * a paged read builds its query instead of writing it, so the same restriction has to be
 * expressible - otherwise a page would quietly return rows the plain list never did.
 * <p>
 * Deliberately not Spring Data's {@code Specification}: this is applied to a count query and to a
 * data query, whose {@code CriteriaQuery} types differ, and the third parameter would be a
 * different object in each - one more thing to get wrong for something no restriction here needs.
 *
 * @param <E> the entity type the query is rooted at
 */
@FunctionalInterface
public interface Restriction<E> {

    /**
     * @param root the query root
     * @param cb   the builder to compose predicates with
     * @return the predicate narrowing the query
     */
    Predicate toPredicate(Root<E> root, CriteriaBuilder cb);
}
