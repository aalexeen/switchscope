package net.switchscope.model.component;

import net.switchscope.model.component.housing.Rack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@code canHoldOtherComponents} answered the opposite of its name: true when the component could
 * not hold anything. Both callers negated it back, so the behaviour was right and only the reading
 * was wrong - which is the kind of defect that stays harmless exactly until someone believes the
 * name. These tests pin the name to the answer, and the two callers to the behaviour they had.
 */
class ContainmentPredicatesTest {

    @Test
    @DisplayName("a component whose type may contain others says so")
    void holdingTypeSaysYes() {
        Rack rack = rackOfType(typeThatContains(true));

        assertThat(rack.canHoldOtherComponents()).isTrue();
    }

    @Test
    @DisplayName("a component whose type may not, and one with no type at all, say no")
    void nonHoldingTypeSaysNo() {
        assertThat(rackOfType(typeThatContains(false)).canHoldOtherComponents()).isFalse();
        assertThat(new Rack().canHoldOtherComponents())
                .as("no type is not knowledge that it may contain something")
                .isFalse();
    }

    @Test
    @DisplayName("and the containment checks built on it are unchanged")
    void containmentChecksKeepTheirAnswers() {
        ComponentTypeEntity childType = typeThatContains(false);
        childType.setCode("CHILD");
        ComponentTypeEntity parentType = typeThatContains(true);
        parentType.getAllowedChildTypeCodes().add("CHILD");

        Rack holder = rackOfType(parentType);
        Rack child = rackOfType(childType);

        assertThat(holder.canContainComponent(child)).isTrue();
        assertThat(holder.canContainComponentType(childType)).isTrue();
        assertThat(holder.canContainComponent(null))
                .as("nothing is not something a component can contain")
                .isFalse();
        assertThat(rackOfType(typeThatContains(false)).canContainComponent(child)).isFalse();
    }

    private static ComponentTypeEntity typeThatContains(boolean canContain) {
        ComponentTypeEntity type = new ComponentTypeEntity();
        type.setCanContainComponents(canContain);
        return type;
    }

    private static Rack rackOfType(ComponentTypeEntity type) {
        Rack rack = new Rack();
        rack.setComponentType(type);
        return rack;
    }
}
