package net.switchscope.web.page;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.hibernate.Hibernate;
import net.switchscope.error.IllegalRequestDataException;
import net.switchscope.to.PageTo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Reads one page of rows and maps it, for every list route in the application.
 * <p>
 * The query is built here rather than added to the existing finders on purpose. Those finders are
 * written as JPQL with {@code LEFT JOIN FETCH}, and several of them fetch a collection -
 * {@code Location.childLocations}, {@code LocationTypeEntity.allowedChildTypes}. Hibernate cannot
 * put a {@code LIMIT} on such a query: it loads every row, paginates the result in memory and says
 * so in a log line nobody reads. Adding {@code Pageable} to those methods would have shipped
 * pagination that does not paginate - the exact shape of defect this project keeps writing tests
 * against. A page therefore selects the rows themselves, with no collection joined, and the
 * associations a DTO needs are loaded while the row is being mapped, inside the service's
 * transaction, in batches of twenty ({@code hibernate.default_batch_fetch_size}).
 * <p>
 * The mapping function is handed in rather than applied by the caller afterwards for the same
 * reason {@code getAllAsDto} exists: mapping outside the transaction that loaded the row throws
 * {@code LazyInitializationException} on the first association a DTO touches.
 */
@Component
public class PageReader {

    /**
     * What to order by when the caller did not say, in order of preference. A page of an unordered
     * collection is not a page: without a total order the database may return a row on two
     * different pages and never on a third, and nothing in the answer would show it.
     */
    private static final List<String> DEFAULT_ORDER = List.of("name", "displayName", "code");

    /** Appended to every ordering, because the fields above are not unique and the id is. */
    private static final String TIEBREAKER = "id";

    private final SortPaths sortPaths;

    @PersistenceContext
    private EntityManager em;

    public PageReader(SortPaths sortPaths) {
        this.sortPaths = sortPaths;
    }

    /**
     * One page of a whole table.
     *
     * @param rowType the entity to read
     * @param query   what the request asked for
     * @param toDto   how one row becomes a DTO; runs inside the caller's transaction
     * @param <E>     the entity type
     * @param <T>     the DTO type
     * @return the requested page
     */
    public <E, T> PageTo<T> read(Class<E> rowType, ListQuery query, Function<E, T> toDto) {
        return read(rowType, null, query, toDto);
    }

    /**
     * One page of the rows a route is about.
     *
     * @param rowType the entity to read
     * @param where   which rows belong to this route, or {@code null} for all of them
     * @param query   what the request asked for
     * @param toDto   how one row becomes a DTO; runs inside the caller's transaction
     * @param <E>     the entity type
     * @param <T>     the DTO type
     * @return the requested page
     */
    public <E, T> PageTo<T> read(Class<E> rowType, Restriction<E> where, ListQuery query,
                                 Function<E, T> toDto) {
        // Read before the query is built: a request that asks for size=0 is answered 422, not
        // answered after the database has already done the work.
        int page = query.pageNumber();
        int size = query.pageSize();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<E> select = cb.createQuery(rowType);
        Root<E> root = select.from(rowType);
        if (where != null) {
            select.where(where.toPredicate(root, cb));
        }
        select.select(root).orderBy(orderBy(root, cb, query));
        TypedQuery<E> rows = em.createQuery(select);

        if (!query.isPaged()) {
            List<T> content = map(rows.getResultList(), toDto);
            return new PageTo<>(content, 0, content.size(), content.size(), 1);
        }

        long offset = (long) page * size;
        if (offset > Integer.MAX_VALUE) {
            throw new IllegalRequestDataException("page " + page + " of size " + size
                    + " starts past any collection this API can hold");
        }
        List<T> content = map(rows.setFirstResult((int) offset).setMaxResults(size).getResultList(), toDto);
        long total = count(rowType, where);
        return new PageTo<>(content, page, size, total, (int) ((total + size - 1) / size));
    }

    /**
     * Each row is unproxied before it is mapped, and that is not belt and braces.
     * <p>
     * This query does not fetch-join the to-one associations the way the plain finders do, so
     * Hibernate resolves them lazily - and a component that is another component's parent gets a
     * proxy created for it while the row that names it is being hydrated. When the result set then
     * reaches that component's own row, the session returns the proxy it already has: an instance
     * of {@code Component$HibernateProxy}, not of {@code Rack}. The polymorphic mappers dispatch
     * with {@code instanceof}, so the row that came back is a component of no known kind, and
     * {@code GET /api/components?page=0} answered 422 until this line existed.
     */
    private static <E, T> List<T> map(List<E> rows, Function<E, T> toDto) {
        return rows.stream().map(PageReader::<E>unproxy).map(toDto).toList();
    }

    @SuppressWarnings("unchecked")
    private static <E> E unproxy(E row) {
        return (E) Hibernate.unproxy(row);
    }

    /**
     * Counted by the database over the same restriction, and without the sort joins: an ordering
     * says nothing about how many rows there are, and a join added for it could only make the
     * count harder to trust.
     */
    private <E> long count(Class<E> rowType, Restriction<E> where) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> select = cb.createQuery(Long.class);
        Root<E> root = select.from(rowType);
        select.select(cb.count(root));
        if (where != null) {
            select.where(where.toPredicate(root, cb));
        }
        return em.createQuery(select).getSingleResult();
    }

    private List<Order> orderBy(Root<?> root, CriteriaBuilder cb, ListQuery query) {
        List<Order> orders = new ArrayList<>();
        for (ListQuery.Ordering ordering : query.orderings()) {
            Path<?> path = sortPaths.path(root, ordering.field());
            orders.add(ordering.ascending() ? cb.asc(path) : cb.desc(path));
        }
        if (orders.isEmpty()) {
            orders.add(cb.asc(defaultPath(root)));
        }
        if (query.orderings().stream().noneMatch(ordering -> TIEBREAKER.equals(ordering.field()))) {
            orders.add(cb.asc(root.get(TIEBREAKER)));
        }
        return orders;
    }

    private Path<?> defaultPath(Root<?> root) {
        for (String candidate : DEFAULT_ORDER) {
            Path<?> path = sortPaths.pathOrNull(root, candidate);
            if (path != null) {
                return path;
            }
        }
        return root.get(TIEBREAKER);
    }
}
