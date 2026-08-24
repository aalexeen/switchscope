package net.switchscope.web.page;

import net.switchscope.to.PageTo;

import java.util.List;
import java.util.function.Supplier;

/**
 * What a list route answers with, given what the caller asked for. One rule, written once, for all
 * twenty of them.
 * <p>
 * <ul>
 *   <li>nothing asked - the whole collection as a bare array, produced by the route's own query,
 *       exactly as before pages existed;</li>
 *   <li>an ordering asked and no page - the whole collection as a bare array, ordered;</li>
 *   <li>a page asked - a {@link PageTo} envelope.</li>
 * </ul>
 * The shape of the answer therefore changes only for a caller that asked for a page, which is what
 * lets the twenty routes gain pagination without a single existing reader of this API breaking.
 * The cost is that a list route's return type is {@code Object}: the choice is made per request, so
 * there is no one static type to declare, and springdoc documents these responses as {@code object}
 * until each route says otherwise.
 */
public final class ListResponse {

    private ListResponse() {
    }

    /**
     * @param query what the request asked for
     * @param plain the route's own query for the whole collection, used when nothing was asked
     * @param paged the paged read, used otherwise
     * @param <T>   the DTO type
     * @return either a {@code List<T>} or a {@code PageTo<T>}
     */
    public static <T> Object of(ListQuery query, Supplier<List<T>> plain, Supplier<PageTo<T>> paged) {
        if (query.isPlain()) {
            return plain.get();
        }
        PageTo<T> page = paged.get();
        return query.isPaged() ? page : page.content();
    }
}
