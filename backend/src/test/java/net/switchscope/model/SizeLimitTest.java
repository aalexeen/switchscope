package net.switchscope.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import net.switchscope.model.installation.Installation;
import net.switchscope.model.location.Location;
import net.switchscope.to.installation.InstallationTo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The three lengths a string has to satisfy - the DTO's, the entity's and the column's - and what
 * happens when they disagree.
 * <p>
 * A DTO limit above the entity's lets a request through the door and fails it at flush, which is a
 * 4xx turned into a 5xx; an entity with no limit at all where the column has one does the same, one
 * layer lower. Both existed: {@code description} was 1024 in the DTO against 512 on the entity over
 * a {@code TEXT} column, and the five {@code Installation} strings the API bounded at 255 or not at
 * all live in {@code VARCHAR(128)} and {@code VARCHAR(256)} columns.
 * <p>
 * Plain Bean Validation rather than an HTTP round trip: what is being checked is the declarations,
 * and one of the three - the column - cannot be reached from a validator anyway, so the assertion
 * that they line up has to name the length the migration gave it.
 */
class SizeLimitTest {

    private static final Validator VALIDATOR;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        VALIDATOR = factory.getValidator();
    }

    @Test
    @DisplayName("a description the DTO accepts is one the entity can hold")
    void descriptionFitsTheEntity() {
        Location location = new Location();
        location.setName("long-description");
        location.setDescription("x".repeat(1024));

        assertThat(VALIDATOR.validateProperty(location, "description"))
                .as("the column is TEXT and the DTO promises 1024; the entity used to stop at 512")
                .isEmpty();
    }

    @Test
    @DisplayName("the entity refuses what its column cannot store")
    void installationStringsAreBoundedByTheirColumns() {
        Installation installation = new Installation();
        installation.setInstalledBy("x".repeat(129));      // installed_by VARCHAR(128)
        installation.setCableManagement("x".repeat(257));  // cable_management VARCHAR(256)

        assertThat(VALIDATOR.validateProperty(installation, "installedBy")).isNotEmpty();
        assertThat(VALIDATOR.validateProperty(installation, "cableManagement")).isNotEmpty();
    }

    @Test
    @DisplayName("and the DTO refuses it first, so the answer is 422 rather than a failed insert")
    void theDtoStopsItAtTheDoor() {
        InstallationTo to = new InstallationTo();
        to.setInstalledBy("x".repeat(129));

        assertThat(VALIDATOR.validateProperty(to, "installedBy"))
                .as("the DTO allowed 255 into a 128-character column")
                .isNotEmpty();
    }
}
