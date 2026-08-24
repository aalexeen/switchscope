package net.switchscope.web.page;

import net.switchscope.error.IllegalRequestDataException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.UUID;

/**
 * Turns the text of a query parameter into the type the column holds.
 * <p>
 * A query string carries only text, and a filter compared as text would either fail in the driver
 * or - worse - match nothing and look like an answer: {@code ?componentTypeId=<uuid>} against a
 * UUID column, {@code ?monitoringEnabled=true} against a boolean. The type comes from the mapping,
 * which is the same source the paths come from, and a value that does not fit it is refused with
 * both the field and the type named, because "422" alone leaves the caller guessing which of their
 * parameters was wrong.
 */
final class FilterValues {

    private FilterValues() {
    }

    /**
     * @param type  the java type of the column being compared
     * @param field the field name as the caller wrote it, for the message
     * @param value the text the caller sent
     * @return the value, as the column's type
     * @throws IllegalRequestDataException if the text is not a value of that type
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    static Object of(Class<?> type, String field, String value) {
        String text = value.trim();
        try {
            if (type == String.class) {
                return value;
            }
            if (type == UUID.class) {
                return UUID.fromString(text);
            }
            if (type == Boolean.class || type == boolean.class) {
                return booleanOf(field, text);
            }
            if (type == Integer.class || type == int.class) {
                return Integer.valueOf(text);
            }
            if (type == Long.class || type == long.class) {
                return Long.valueOf(text);
            }
            if (type == Short.class || type == short.class) {
                return Short.valueOf(text);
            }
            if (type == Double.class || type == double.class) {
                return Double.valueOf(text);
            }
            if (type == Float.class || type == float.class) {
                return Float.valueOf(text);
            }
            if (type == BigDecimal.class) {
                return new BigDecimal(text);
            }
            if (type == LocalDate.class) {
                return LocalDate.parse(text);
            }
            if (type == LocalDateTime.class) {
                return LocalDateTime.parse(text);
            }
            if (type == OffsetDateTime.class) {
                return OffsetDateTime.parse(text);
            }
            if (type == Instant.class) {
                return Instant.parse(text);
            }
            if (type.isEnum()) {
                return enumOf((Class<? extends Enum>) type, field, text);
            }
        } catch (IllegalArgumentException | DateTimeParseException wrongShape) {
            throw refuse(type, field, value);
        }
        throw new IllegalRequestDataException("cannot filter by '" + field + "': it holds "
                + type.getSimpleName() + ", which a query parameter cannot express");
    }

    private static Boolean booleanOf(String field, String text) {
        if ("true".equalsIgnoreCase(text)) {
            return Boolean.TRUE;
        }
        if ("false".equalsIgnoreCase(text)) {
            return Boolean.FALSE;
        }
        // Boolean.valueOf would answer false to anything at all, including "yes" and "1", and the
        // caller would get rows they did not ask for with nothing to tell them why.
        throw refuse(Boolean.class, field, text);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Enum enumOf(Class<? extends Enum> type, String field, String text) {
        for (Enum constant : type.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(text)) {
                return constant;
            }
        }
        throw new IllegalRequestDataException("filter '" + field + "' expects one of "
                + java.util.Arrays.toString(type.getEnumConstants()) + ", got '" + text + "'");
    }

    private static IllegalRequestDataException refuse(Class<?> type, String field, String value) {
        return new IllegalRequestDataException("filter '" + field + "' expects "
                + type.getSimpleName() + ", got '" + value + "'");
    }
}
