package net.switchscope.to;

import java.util.List;
import java.util.function.Function;

/**
 * One page of a list route, and enough about the whole collection to ask for the next one.
 * <p>
 * A list route answers with a bare array unless the request asked for a page - see
 * {@link net.switchscope.web.page.ListResponse} - so this envelope is what a client opts into, not
 * what every reader suddenly has to unwrap.
 * <p>
 * {@code totalElements} is counted by the database over the same restriction as the page itself,
 * not derived from what came back: a page holds at most {@code size} rows and therefore knows
 * nothing about how many there are. Without that count a client cannot tell "the last page" from
 * "a page that happens to be short".
 *
 * @param content       the rows of this page, already mapped to DTOs
 * @param page          zero-based index of this page
 * @param size          how many rows a full page holds
 * @param totalElements how many rows the collection holds in total
 * @param totalPages    how many pages of {@code size} the collection makes
 * @param <T>           the DTO type
 */
public record PageTo<T>(List<T> content, int page, int size, long totalElements, int totalPages) {

    /**
     * The same page with every row put through {@code mapper}, for a caller that produces its DTOs
     * itself. The counts describe the collection, not the mapping, so they carry over untouched.
     *
     * @param mapper what to apply to each row
     * @param <R>    the resulting row type
     * @return a page of mapped rows
     */
    public <R> PageTo<R> map(Function<? super T, ? extends R> mapper) {
        return new PageTo<>(content.stream().<R>map(mapper).toList(),
                page, size, totalElements, totalPages);
    }
}
