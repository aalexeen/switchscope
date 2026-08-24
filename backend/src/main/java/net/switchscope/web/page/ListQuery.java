package net.switchscope.web.page;

import net.switchscope.error.IllegalRequestDataException;

import java.util.List;

/**
 * What a list route was asked for: which page, how large, and in what order.
 * <p>
 * Bound from the query string as a model attribute, so the three parameters are declared once here
 * instead of on each of the twenty list routes. A request that carries none of them is
 * {@link #isPlain()}, and such a request is answered exactly as it was before this class existed -
 * the whole collection, as a bare array. That is the reason the parameters are all optional: a page
 * is something a client asks for, never something the server starts imposing on readers that were
 * written against the old answer.
 * <p>
 * The values are validated where they are read rather than by bean validation, because the messages
 * matter: "size must be between 1 and 200" tells the caller what to send next, whereas a rejected
 * request with no reason sends them to the source.
 */
public class ListQuery {

    /** How large a page is when the caller asks for one without saying. */
    public static final int DEFAULT_SIZE = 20;

    /**
     * The largest page a caller may ask for. A cap that silently truncated would be worse than no
     * cap at all - the client would page through a collection whose pages lie about their size -
     * so asking for more is refused, not trimmed.
     */
    public static final int MAX_SIZE = 200;

    /** The separator between a sort field and its direction: {@code name:desc}. */
    private static final String DIRECTION_SEPARATOR = ":";

    private Integer page;
    private Integer size;
    private List<String> sort = List.of();

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public List<String> getSort() {
        return sort;
    }

    public void setSort(List<String> sort) {
        this.sort = sort == null ? List.of() : sort;
    }

    /** @return whether the caller asked for a page rather than the whole collection */
    public boolean isPaged() {
        return page != null || size != null;
    }

    /** @return whether the caller asked for nothing at all, and gets the answer the route always gave */
    public boolean isPlain() {
        return !isPaged() && sort.isEmpty();
    }

    /** @return the zero-based page index, defaulting to the first page */
    public int pageNumber() {
        if (page == null) {
            return 0;
        }
        if (page < 0) {
            throw new IllegalRequestDataException("page must not be negative, got " + page);
        }
        return page;
    }

    /** @return how many rows the page holds, defaulting to {@link #DEFAULT_SIZE} */
    public int pageSize() {
        if (size == null) {
            return DEFAULT_SIZE;
        }
        if (size < 1 || size > MAX_SIZE) {
            throw new IllegalRequestDataException(
                    "size must be between 1 and " + MAX_SIZE + ", got " + size);
        }
        return size;
    }

    /**
     * The requested ordering, in the order it was given, as {@code field} and direction pairs.
     * <p>
     * The direction is written after a colon ({@code sort=name:desc}) rather than after a comma,
     * which is the more usual spelling, because Spring splits a repeated query parameter on commas:
     * {@code sort=name,desc} arrives as two values, and the second one would be read as a field
     * named "desc". Colon-separated, one parameter is one ordering, and several orderings can still
     * be written either as repeated parameters or comma-separated.
     *
     * @return the orderings asked for, empty when the caller asked for none
     */
    public List<Ordering> orderings() {
        return sort.stream().map(ListQuery::parseOrdering).toList();
    }

    private static Ordering parseOrdering(String spec) {
        String[] parts = spec.split(DIRECTION_SEPARATOR, 2);
        String field = parts[0].trim();
        if (field.isEmpty()) {
            throw new IllegalRequestDataException("sort needs a field name, got '" + spec + "'");
        }
        if (parts.length == 1) {
            return new Ordering(field, true);
        }
        String direction = parts[1].trim().toLowerCase();
        return switch (direction) {
            case "asc" -> new Ordering(field, true);
            case "desc" -> new Ordering(field, false);
            default -> throw new IllegalRequestDataException(
                    "sort direction must be asc or desc, got '" + parts[1] + "'"
                            + " - the form is sort=<field>[:asc|:desc]");
        };
    }

    @Override
    public String toString() {
        return isPlain() ? "whole list" : "page=" + page + " size=" + size + " sort=" + sort;
    }

    /**
     * One ordering: a field, named as the DTO names it, and which way to read it.
     *
     * @param field     the field to order by
     * @param ascending whether smaller values come first
     */
    public record Ordering(String field, boolean ascending) {
    }
}
