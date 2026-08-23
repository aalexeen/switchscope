package net.switchscope.security.policy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import net.switchscope.error.IllegalRequestDataException;
import net.switchscope.error.PolicyViolationException;
import net.switchscope.model.component.ComponentNatureEntity;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.model.component.device.AccessPoint;
import net.switchscope.model.component.housing.Rack;
import net.switchscope.model.location.Location;
import net.switchscope.model.location.catalog.LocationTypeEntity;
import net.switchscope.to.component.device.AccessPointTo;
import net.switchscope.to.component.housing.RackTo;
import net.switchscope.to.location.LocationTo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * The half of the field-access layer that was missing: what actually happens to a field the caller
 * sent as {@code null}.
 * <p>
 * {@link UpdatePolicyValidator} decides <em>who may</em> clear a field, from annotations on the
 * DTO. That is not enough to clear one safely, and the gap is not hypothetical: {@code ComponentTo}
 * marks {@code manufacturer} {@link FieldAccessLevel#ADMIN_NULLABLE} while the column behind it is
 * NOT NULL, and any DTO field nobody annotates defaults to admin-clearable whatever the schema
 * says. Writing such a null would be a constraint violation: a 500 for a request the API had just
 * accepted. Hence the second gate, on the entity, which these tests are mostly about.
 * <p>
 * Plain unit tests, deliberately: every case here is decided by the JPA mapping and by reflection,
 * so a database would add setup without adding evidence.
 */
class NullFieldApplierTest {

    private static final JsonNodeFactory NODES = JsonNodeFactory.instance;

    private static final UpdatePolicy ADMIN = new AdminUpdatePolicy();

    private final FieldAccessMetadataCache metadata = new FieldAccessMetadataCache();
    private final NullFieldApplier applier =
            new NullFieldApplier(metadata, new EntityNullability(), new UpdatePolicyValidator(metadata));

    private void apply(Object entity, Class<?> dtoClass, Map<String, JsonNode> presentFields) {
        applier.apply(entity, dtoClass, presentFields, ADMIN);
    }

    private static Map<String, JsonNode> nulls(String... fieldNames) {
        return java.util.Arrays.stream(fieldNames)
                .collect(java.util.stream.Collectors.toMap(name -> name, name -> NODES.nullNode()));
    }

    @Test
    @DisplayName("an optional scalar the payload sent as null is cleared")
    void clearsOptionalScalar() {
        Location location = new Location();
        location.setAddress("12 Somewhere Street");

        apply(location, LocationTo.class, nulls("address"));

        assertThat(location.getAddress()).isNull();
    }

    @Test
    @DisplayName("a field the payload did not mention is left alone")
    void leavesAbsentFieldsAlone() {
        Location location = new Location();
        location.setAddress("12 Somewhere Street");

        apply(location, LocationTo.class, Map.of("name", NODES.textNode("depot")));

        assertThat(location.getAddress())
                .as("only fields present in the payload with a null value are cleared; a value, or"
                        + " an absent field, must not be")
                .isEqualTo("12 Somewhere Street");
    }

    @Test
    @DisplayName("an optional foreign key is cleared through its <name>Id field")
    void clearsOptionalForeignKey() {
        Rack rack = new Rack();
        rack.setComponentNature(new ComponentNatureEntity());

        apply(rack, RackTo.class, nulls("componentNatureId"));

        assertThat(rack.getComponentNature())
                .as("the DTO carries componentNatureId where the entity carries componentNature;"
                        + " clearing an association is that name with the Id suffix dropped")
                .isNull();
    }

    @Test
    @DisplayName("a REQUIRED field is refused by the policy, before possibility is even considered")
    void refusesRequiredField() {
        Rack rack = new Rack();
        rack.setComponentType(new ComponentTypeEntity());

        assertThatThrownBy(() -> apply(rack, RackTo.class, nulls("componentTypeId")))
                .as("nobody may clear a REQUIRED field, so this is a 403 about the caller rather"
                        + " than a 422 about the column - even for an admin")
                .isInstanceOf(PolicyViolationException.class);
        assertThat(rack.getComponentType())
                .as("the request is refused before anything is written")
                .isNotNull();
    }

    @Test
    @DisplayName("a NOT NULL foreign key is refused through the <name>Id mapping")
    void refusesMandatoryForeignKey() {
        Stored stored = new Stored();
        stored.setMandatoryRef(new Object());

        assertThatThrownBy(() -> apply(stored, StoredTo.class, nulls("mandatoryRefId")))
                .isInstanceOf(IllegalRequestDataException.class)
                .hasMessageContaining("mandatoryRefId")
                .hasMessageContaining("NOT NULL");
        assertThat(stored.getMandatoryRef()).isNotNull();
    }

    @Test
    @DisplayName("a NOT NULL column is refused even though the DTO says an admin may clear it")
    void refusesMandatoryColumn() {
        Rack rack = new Rack();
        rack.setManufacturer("Acme");

        assertThatThrownBy(() -> apply(rack, RackTo.class, nulls("manufacturer")))
                .as("this is the two gates disagreeing, which is the case the entity gate exists"
                        + " for: ComponentTo marks manufacturer ADMIN_NULLABLE, so the policy lets"
                        + " an admin through, and the column behind it is NOT NULL")
                .isInstanceOf(IllegalRequestDataException.class)
                .hasMessageContaining("NOT NULL");
        assertThat(rack.getManufacturer()).isEqualTo("Acme");
    }

    @Test
    @DisplayName("a primitive property is refused - null is not a value it can hold")
    void refusesPrimitive() {
        Stored stored = new Stored();
        stored.setCount(7);

        assertThatThrownBy(() -> apply(stored, StoredTo.class, nulls("count")))
                .isInstanceOf(IllegalRequestDataException.class)
                .hasMessageContaining("primitive");
        assertThat(stored.getCount()).isEqualTo(7);
    }

    @Test
    @DisplayName("a null over a value that is already empty asks for nothing and needs no policy")
    void ignoresNullsThatChangeNothing() {
        Location location = new Location();
        location.setAddress("12 Somewhere Street");

        applier.apply(location, LocationTo.class, nulls("description", "roomNumber"),
                new UserUpdatePolicy());

        assertThat(location.getDescription())
                .as("description is ADMIN_NULLABLE and this caller is a USER, but both fields are"
                        + " already empty - refusing here would block the detail view's ordinary"
                        + " save, which echoes the object back with its empty optionals included")
                .isNull();
        assertThat(location.getAddress()).isEqualTo("12 Somewhere Street");
    }

    @Test
    @DisplayName("the same null over a value that is set is refused for that caller")
    void refusesNullsThatDoChangeSomething() {
        Location location = new Location();
        location.setDescription("something worth keeping");

        assertThatThrownBy(() -> applier.apply(location, LocationTo.class, nulls("description"),
                new UserUpdatePolicy()))
                .as("the exemption above is about the null changing nothing, not about the policy"
                        + " having been relaxed")
                .isInstanceOf(PolicyViolationException.class);
        assertThat(location.getDescription()).isEqualTo("something worth keeping");
    }

    @Test
    @DisplayName("a managed collection is refused rather than replaced with null")
    void refusesCollection() {
        AccessPoint accessPoint = new AccessPoint();
        accessPoint.getSsids().add("guest");

        assertThatThrownBy(() -> apply(accessPoint, AccessPointTo.class, nulls("ssids")))
                .as("Hibernate tracks a loaded collection through its own wrapper; swapping in null"
                        + " throws that wrapper away and with it the state of the association")
                .isInstanceOf(IllegalRequestDataException.class)
                .hasMessageContaining("collection");
        assertThat(accessPoint.getSsids()).containsExactly("guest");
    }

    @Test
    @DisplayName("a list of ids has no single property behind it and is skipped")
    void skipsIdCollectionFields() {
        Location location = new Location();
        location.setAddress("12 Somewhere Street");

        apply(location, LocationTo.class, nulls("childLocationIds"));

        assertThat(location.getChildLocations())
                .as("childLocationIds is a projection of the association, not the association;"
                        + " the <name>Id convention covers a single reference and nothing else")
                .isEmpty();
        assertThat(location.getAddress()).isEqualTo("12 Somewhere Street");
    }

    @Test
    @DisplayName("a response-only field with nothing stored behind it is skipped in silence")
    void skipsFieldsThatAreNotStored() {
        Location location = new Location();
        location.setAddress("12 Somewhere Street");

        apply(location, LocationTo.class, nulls("typeCode", "parentLocationName"));

        assertThat(location.getAddress())
                .as("a payload echoing back what a GET returned carries derived fields too;"
                        + " there is nothing to clear behind them and that is not an error")
                .isEqualTo("12 Somewhere Street");
    }

    @Test
    @DisplayName("a READ_ONLY field is skipped, exactly as the validator skips it")
    void skipsReadOnlyFields() {
        Stored stored = new Stored();
        stored.setDerived("computed");
        stored.setPlain("editable");

        apply(stored, StoredTo.class, nulls("derived", "plain"));

        assertThat(stored.getDerived())
                .as("UpdatePolicyValidator passes over READ_ONLY without complaining, so clearing"
                        + " one here would apply what the validator declined to rule on")
                .isEqualTo("computed");
        assertThat(stored.getPlain())
                .as("the same call clears the field that is not READ_ONLY, so the assertion above"
                        + " is about the level and not about the call having done nothing")
                .isNull();
    }

    /**
     * A pair made for this test rather than borrowed from the model. Every READ_ONLY field in the
     * DTOs today is a derived one - {@code componentStatusCode} and friends - which the applier
     * would skip anyway for having nothing stored behind it, so borrowing one would prove the wrong
     * branch.
     */
    static class Stored {
        private String derived;
        private String plain;
        private int count;
        @jakarta.persistence.JoinColumn(name = "mandatory_ref_id", nullable = false)
        private Object mandatoryRef;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public Object getMandatoryRef() {
            return mandatoryRef;
        }

        public void setMandatoryRef(Object mandatoryRef) {
            this.mandatoryRef = mandatoryRef;
        }

        public String getDerived() {
            return derived;
        }

        public void setDerived(String derived) {
            this.derived = derived;
        }

        public String getPlain() {
            return plain;
        }

        public void setPlain(String plain) {
            this.plain = plain;
        }
    }

    static class StoredTo {
        @FieldAccess(FieldAccessLevel.READ_ONLY)
        private String derived;
        @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
        private String plain;
        @FieldAccess(FieldAccessLevel.USER_WRITABLE)
        private Integer count;
        @FieldAccess(FieldAccessLevel.USER_WRITABLE)
        private java.util.UUID mandatoryRefId;
    }
}
