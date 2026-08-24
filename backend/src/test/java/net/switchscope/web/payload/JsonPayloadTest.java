package net.switchscope.web.payload;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.switchscope.error.MalformedRequestException;
import net.switchscope.mapper.component.device.AccessPointMapper;
import net.switchscope.model.component.device.AccessPoint;
import net.switchscope.to.component.device.AccessPointTo;
import net.switchscope.to.component.ComponentTo;
import net.switchscope.to.component.housing.RackTo;
import net.switchscope.to.location.LocationTo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Binding a partial body to a concrete subtype.
 * <p>
 * {@code RackTo} inherits {@code @JsonTypeInfo} from {@code ComponentTo}, so Jackson demands
 * {@code componentClass} even when the declared target already says which subtype it is. That made
 * {@code PUT /api/housing/racks/{id}} with {@code {"description": "x"}} answer 500
 * ({@code InvalidTypeIdException}) - on nine routes, for any payload that did not echo the whole
 * object back. Three polymorphic controllers had each worked around it by hand for their own
 * hierarchy; the workaround now lives here and applies to all of them.
 */
class JsonPayloadTest {

    private final JsonPayload json = new JsonPayload(new ObjectMapper());

    @Test
    @DisplayName("a partial body binds to a polymorphic subtype without carrying its discriminator")
    void bindsPartialPayloadToSubtype() {
        RackTo bound = json.bind(json.asObject("""
                {"description": "a partial update"}"""), RackTo.class);

        assertThat(bound.getDescription()).isEqualTo("a partial update");
        assertThat(bound.getComponentClass())
                .as("the target type is the authority on what this is, so the discriminator is"
                        + " filled in from it rather than demanded of the caller")
                .isEqualTo("RACK");
    }

    @Test
    @DisplayName("the payload cannot rename the type it is being bound to")
    void payloadCannotOverrideTheSubtype() {
        RackTo bound = json.bind(json.asObject("""
                {"componentClass": "ROUTER", "description": "not a router"}"""), RackTo.class);

        assertThat(bound.getComponentClass())
                .as("the route decided this is a rack; a body claiming otherwise is overwritten,"
                        + " not obeyed, which is the same rule the polymorphic controllers apply"
                        + " when they pin the stored discriminator")
                .isEqualTo("RACK");
    }

    @Test
    @DisplayName("an abstract base is left to the payload, which is how create still works")
    void abstractBaseKeepsThePayloadDiscriminator() {
        ComponentTo bound = json.bind(json.asObject("""
                {"componentClass": "RACK", "name": "rack-1"}"""), ComponentTo.class);

        assertThat(bound)
                .as("ComponentTo is registered under no name of its own, so nothing is pinned and"
                        + " the discriminator the create path derived is what decides")
                .isInstanceOf(RackTo.class);
    }

    @Test
    @DisplayName("a non-polymorphic DTO is bound untouched")
    void plainDtoIsUnaffected() {
        LocationTo bound = json.bind(json.asObject("""
                {"address": "12 Somewhere Street"}"""), LocationTo.class);

        assertThat(bound.getAddress()).isEqualTo("12 Somewhere Street");
    }

    @Test
    @DisplayName("present fields keep absent and explicitly-null apart")
    void presentFieldsDistinguishesNullFromAbsent() {
        var fields = json.presentFields(json.asObject("""
                {"address": null, "floorNumber": 3}"""));

        assertThat(fields).containsOnlyKeys("address", "floorNumber");
        assertThat(fields.get("address").isNull()).isTrue();
    }

    @Test
    @DisplayName("a collection the payload never mentioned is not emptied by the update mapper")
    void absentCollectionSurvivesTheMapper() {
        AccessPoint stored = new AccessPoint();
        stored.getSsids().addAll(java.util.List.of("Corporate-WiFi", "Guest-WiFi"));

        Mappers.getMapper(AccessPointMapper.class)
                .updateFromTo(stored, read("{\"description\": \"a partial update\"}", AccessPointTo.class));

        assertThat(stored.getSsids())
                .as("AccessPointTo.ssids is declared = new HashSet<>(), so before blanking it"
                        + " reached the mapper as an empty set and the generated update cleared the"
                        + " stored SSIDs and added nothing back - measured on a live PUT, not"
                        + " theorised")
                .containsExactlyInAnyOrder("Corporate-WiFi", "Guest-WiFi");
        assertThat(stored.getDescription()).isEqualTo("a partial update");
    }

    @Test
    @DisplayName("an explicitly empty collection still empties it - the caller asked for that")
    void explicitlyEmptyCollectionIsApplied() {
        AccessPoint stored = new AccessPoint();
        stored.getSsids().addAll(java.util.List.of("Corporate-WiFi"));

        Mappers.getMapper(AccessPointMapper.class)
                .updateFromTo(stored, read("{\"ssids\": []}", AccessPointTo.class));

        assertThat(stored.getSsids())
                .as("blanking restores what absence means; it must not swallow a value the payload"
                        + " actually carried")
                .isEmpty();
    }

    /**
     * The reading a PUT handler does, minus the policy: pin the type, note what came, bind, and put
     * back the absence that the DTO's field initialisers hid.
     */
    private <T> T read(String body, Class<T> dtoClass) {
        var root = json.asObject(body);
        json.pinDiscriminator(root, dtoClass);
        var present = json.presentFields(root);
        T dto = json.bind(root, dtoClass);
        json.blankAbsentProperties(dto, present.keySet());
        return dto;
    }

    @Test
    @DisplayName("a body that is not a JSON object is 400 - unreadable, not merely refused")
    void rejectsNonObjectBodies() {
        assertThatThrownBy(() -> json.asObject("[1, 2, 3]"))
                .isInstanceOf(MalformedRequestException.class)
                .hasMessageContaining("JSON object");
        assertThatThrownBy(() -> json.asObject("{not json"))
                .isInstanceOf(MalformedRequestException.class)
                .hasMessageContaining("valid JSON");
    }
}
