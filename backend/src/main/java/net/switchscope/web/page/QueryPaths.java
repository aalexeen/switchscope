package net.switchscope.web.page;

import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.Type;
import net.switchscope.error.IllegalRequestDataException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Turns a field name a client uses into a path the database can order by, compare or match - and
 * refuses the ones it cannot reach.
 * <p>
 * The names clients know are the DTO's, and a DTO here is denormalised: {@code ComponentTo} carries
 * {@code componentTypeCode}, {@code parentComponentName}, {@code componentPath}. Some of those are
 * a column, some are one association away from one, and some are computed and correspond to no
 * column at all. So the answer cannot be a hand-kept list per controller - it would be wrong the
 * first time a field was added - and it cannot be "whatever the client sends" either.
 * <p>
 * Sorting, filtering and searching all ask this same question and get the same answer, which is
 * what makes {@code ?sort=componentTypeCode} and {@code ?componentTypeCode=RACK} mean the same
 * field. Only the refusal differs, because the messages are worth being specific about.
 * <p>
 * It is read from the JPA metamodel instead, which is the same source the rest of this project
 * takes such answers from (see {@code EntityNullability}), and the rule is one sentence: a field is
 * sortable when it is a persistent scalar of the row, or a persistent scalar of something the row
 * points at through a single association, named the way the DTO names it -
 * {@code componentType} + {@code code} spells {@code componentTypeCode}. Anything else is refused
 * <em>by name</em>, because a sort or a filter the server silently ignores is the kind of feature
 * that reads as working right up until someone checks - and an ignored filter answers with rows
 * the caller excluded.
 * <p>
 * The association hop is a LEFT JOIN, and that is not cosmetic: {@code root.get("parentComponent")}
 * would produce an inner join, and ordering by the name of a parent that most components do not
 * have would then drop them from the answer - a sort that also filters.
 */
@Component
public class QueryPaths {

    /**
     * Where to order by, for a field named as the DTO names it.
     *
     * @param root  the query root
     * @param field the field name the client asked to sort by
     * @return the expression to order by
     * @throws IllegalRequestDataException if no persistent path corresponds to that name
     */
    public Path<?> sortPath(Root<?> root, String field) {
        return path(root, field, "sort by");
    }

    /**
     * Where to compare, for a field named as the DTO names it. The same rule as sorting: a filter
     * a client can write is a value the row has or a value one association away, and a filter on
     * anything else is refused rather than dropped - a filter silently ignored answers with rows
     * the caller did not ask for and says nothing about it.
     *
     * @param root  the query root
     * @param field the field name the client asked to filter by
     * @return the expression to compare
     * @throws IllegalRequestDataException if no persistent path corresponds to that name
     */
    public Path<?> filterPath(Root<?> root, String field) {
        return path(root, field, "filter by");
    }

    /**
     * Where to match text, for a field named as the DTO names it. Refuses a field that is not text:
     * "contains" has no meaning for a boolean, and answering as though it did would be worse than
     * saying so.
     *
     * @param root  the query root
     * @param field the field name to search in
     * @return the text expression to match
     * @throws IllegalRequestDataException if the field is not a text value of the row
     */
    public Path<?> searchPath(Root<?> root, String field) {
        Path<?> path = path(root, field, "search in");
        if (!String.class.equals(path.getJavaType())) {
            throw new IllegalRequestDataException("cannot search in '" + field + "': it holds "
                    + path.getJavaType().getSimpleName() + ", and searching is over text");
        }
        return path;
    }

    /**
     * Every text value of the row itself, which is what a search covers when the caller does not
     * name the fields. Read from the mapping rather than kept as a list per route, for the same
     * reason the paths are.
     *
     * @param root the query root
     * @return the names of the row's own text values
     */
    public List<String> textFields(Root<?> root) {
        return root.getModel().getAttributes().stream()
                .filter(QueryPaths::isValue)
                .filter(attribute -> String.class.equals(attribute.getJavaType()))
                .map(Attribute::getName)
                .sorted()
                .toList();
    }

    private Path<?> path(Root<?> root, String field, String use) {
        Path<?> resolved = pathOrNull(root, field);
        if (resolved != null) {
            return resolved;
        }
        ManagedType<?> rowType = root.getModel();
        if (attribute(rowType, field) != null) {
            throw new IllegalRequestDataException("cannot " + use + " '" + field
                    + "': it is an association or a collection, not a value; use one of its"
                    + " properties instead, as in " + field + "Code");
        }
        throw new IllegalRequestDataException("cannot " + use + " '" + field + "': "
                + rowType.getJavaType().getSimpleName() + " has no such value; it has "
                + valueNames(rowType) + ", plus a property of "
                + toOneAssociations(rowType).stream().map(Attribute::getName).toList()
                + " written as one name, e.g. componentTypeCode");
    }

    /**
     * The same resolution without a verdict, for asking whether a field exists - the default
     * ordering tries a few names and takes the first the row actually has.
     *
     * @param root  the query root
     * @param field the field name
     * @return the expression to order by, or {@code null} if the row has no such value
     */
    public Path<?> pathOrNull(Root<?> root, String field) {
        ManagedType<?> rowType = root.getModel();
        Attribute<?, ?> direct = attribute(rowType, field);
        if (direct != null) {
            return isValue(direct) ? root.get(field) : null;
        }
        for (Attribute<?, ?> association : toOneAssociations(rowType)) {
            String prefix = association.getName();
            if (!field.startsWith(prefix) || field.length() == prefix.length()) {
                continue;
            }
            ManagedType<?> target = targetOf(association);
            if (target == null) {
                continue;
            }
            String property = decapitalise(field.substring(prefix.length()));
            Attribute<?, ?> nested = attribute(target, property);
            if (nested != null && isValue(nested)) {
                return leftJoin(root, prefix).get(property);
            }
        }
        return null;
    }

    /**
     * A join is reused rather than added twice when two orderings go through the same association:
     * two joins to the same table would multiply nothing (the association is to-one) but would say
     * in SQL something the query does not mean.
     */
    private static From<?, ?> leftJoin(Root<?> root, String attribute) {
        for (Join<?, ?> existing : root.getJoins()) {
            if (existing.getAttribute().getName().equals(attribute)
                    && existing.getJoinType() == JoinType.LEFT) {
                return existing;
            }
        }
        return root.join(attribute, JoinType.LEFT);
    }

    /**
     * The associations that hold at most one row, which are the ones a query may fetch without
     * giving up on {@code LIMIT}.
     *
     * @param root the query root
     * @return the names of the row's to-one associations
     */
    public List<String> toOneNames(Root<?> root) {
        return toOneAssociations(root.getModel()).stream().map(Attribute::getName).sorted().toList();
    }

    /** Longest name first, so {@code parentComponentName} is read as the parent's name, not the parent of "ComponentName". */
    private static List<Attribute<?, ?>> toOneAssociations(ManagedType<?> type) {
        List<Attribute<?, ?>> found = new ArrayList<>();
        for (Attribute<?, ?> attribute : type.getAttributes()) {
            if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.MANY_TO_ONE
                    || attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.ONE_TO_ONE) {
                found.add(attribute);
            }
        }
        found.sort(Comparator.comparingInt((Attribute<?, ?> a) -> a.getName().length()).reversed());
        return found;
    }

    private static List<String> valueNames(ManagedType<?> type) {
        return type.getAttributes().stream()
                .filter(QueryPaths::isValue)
                .map(Attribute::getName)
                .sorted()
                .toList();
    }

    private static Attribute<?, ?> attribute(ManagedType<?> type, String name) {
        try {
            return type.getAttribute(name);
        } catch (IllegalArgumentException noSuchAttribute) {
            return null;
        }
    }

    private static boolean isValue(Attribute<?, ?> attribute) {
        return attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.BASIC;
    }

    private static ManagedType<?> targetOf(Attribute<?, ?> association) {
        Type<?> target = ((SingularAttribute<?, ?>) association).getType();
        return target instanceof ManagedType<?> managed ? managed : null;
    }

    private static String decapitalise(String name) {
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }
}
