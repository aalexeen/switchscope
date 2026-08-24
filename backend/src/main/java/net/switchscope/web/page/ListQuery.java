package net.switchscope.web.page;

import net.switchscope.error.IllegalRequestDataException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * What a list route was asked for: which page, how large, in what order, and of which rows.
 * <p>
 * Built from the whole query string by {@link ListQueryArgumentResolver}, not bound field by field,
 * because the filters are the parameters this class does <em>not</em> know the names of: a filter
 * is any parameter that is not one of the five reserved names, and a bound object cannot be handed
 * what it has no field for. Reading the parameter map is also what makes a mistyped parameter
 * name reachable at all - it arrives as a filter on a field the row does not have, and is refused
 * by name rather than dropped.
 * <p>
 * A request that carries none of them is {@link #isPlain()}, and such a request is answered exactly
 * as it was before this class existed - the whole collection, as a bare array. That is why every
 * parameter is optional: a page is something a client asks for, never something the server starts
 * imposing on readers written against the old answer.
 * <p>
 * A parameter with an empty value counts as absent. A cleared search box sends {@code ?search=},
 * and reading that as "rows whose text is the empty string" would answer nothing to a UI that
 * meant to ask for everything.
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

    /** The parameters that mean something to every route, and are therefore never filters. */
    static final List<String> RESERVED = List.of("page", "size", "sort", "search", "searchIn");

    /** The separator between a sort field and its direction: {@code name:desc}. */
    private static final String DIRECTION_SEPARATOR = ":";

    private final Integer page;
    private final Integer size;
    private final List<String> sort;
    private final String search;
    private final List<String> searchIn;
    private final Map<String, List<String>> filters;

    ListQuery(Integer page, Integer size, List<String> sort, String search, List<String> searchIn,
              Map<String, List<String>> filters) {
        this.page = page;
        this.size = size;
        this.sort = sort;
        this.search = search;
        this.searchIn = searchIn;
        this.filters = filters;
    }

    /** A request that asked for nothing, used where no query string is involved. */
    public static ListQuery whole() {
        return new ListQuery(null, null, List.of(), null, List.of(), Map.of());
    }

    public Integer getPage() {
        return page;
    }

    public Integer getSize() {
        return size;
    }

    public List<String> getSort() {
        return sort;
    }

    public String getSearch() {
        return search;
    }

    public List<String> getSearchIn() {
        return searchIn;
    }

    /**
     * Every parameter that is not one of the reserved names, as field to values. Deliberately not a
     * JavaBean getter: springdoc documents this object's properties as query parameters, and
     * "filters" is not one - the fields a route accepts are the fields its row has.
     *
     * @return the filters asked for, empty when none were
     */
    Map<String, List<String>> filters() {
        return filters;
    }

    /** @return whether the caller asked for a page rather than the whole collection */
    public boolean isPaged() {
        return page != null || size != null;
    }

    /** @return whether the caller asked for nothing at all, and gets the answer the route always gave */
    public boolean isPlain() {
        return !isPaged() && sort.isEmpty() && search == null && filters.isEmpty();
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
     * which is the more usual spelling, because a comma already separates one ordering from the
     * next: {@code sort=name:desc,code} is two of them. Written the usual way, {@code sort=name,desc}
     * asks to order by a field named "desc" and is refused as one.
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

    /**
     * Reads the query string. Values that are empty are dropped first, so a parameter left blank by
     * a form is the same as a parameter not sent.
     *
     * @param parameters the request's parameter map
     * @return what the request asked for
     */
    static ListQuery of(Map<String, String[]> parameters) {
        Map<String, List<String>> given = new LinkedHashMap<>();
        parameters.forEach((name, values) -> {
            List<String> present = Arrays.stream(values).filter(value -> !value.isBlank()).toList();
            if (!present.isEmpty()) {
                given.put(name, present);
            }
        });

        Map<String, List<String>> filters = new LinkedHashMap<>(given);
        RESERVED.forEach(filters::remove);

        return new ListQuery(
                number(given, "page"),
                number(given, "size"),
                commaSeparated(given, "sort"),
                single(given, "search"),
                commaSeparated(given, "searchIn"),
                Map.copyOf(filters));
    }

    private static Integer number(Map<String, List<String>> given, String name) {
        String value = single(given, name);
        if (value == null) {
            return null;
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException notANumber) {
            throw new IllegalRequestDataException(name + " must be a whole number, got '" + value + "'");
        }
    }

    private static String single(Map<String, List<String>> given, String name) {
        List<String> values = given.get(name);
        return values == null ? null : values.get(values.size() - 1);
    }

    /**
     * Several values, whether written as repeated parameters or separated by commas. Used for the
     * two parameters whose values are field names, which never contain a comma; a filter's value
     * may, so filters are not split.
     */
    private static List<String> commaSeparated(Map<String, List<String>> given, String name) {
        List<String> values = given.get(name);
        if (values == null) {
            return List.of();
        }
        List<String> split = new ArrayList<>();
        for (String value : values) {
            for (String part : value.split(",")) {
                if (!part.isBlank()) {
                    split.add(part.trim());
                }
            }
        }
        return List.copyOf(split);
    }

    @Override
    public String toString() {
        if (isPlain()) {
            return "whole list";
        }
        StringBuilder said = new StringBuilder();
        if (isPaged()) {
            said.append("page=").append(page).append(" size=").append(size).append(' ');
        }
        if (!sort.isEmpty()) {
            said.append("sort=").append(sort).append(' ');
        }
        if (search != null) {
            said.append("search='").append(search).append('\'')
                    .append(searchIn.isEmpty() ? "" : " in " + searchIn).append(' ');
        }
        if (!filters.isEmpty()) {
            said.append("where ").append(filters);
        }
        return said.toString().trim();
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
