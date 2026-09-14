package de.mediasystem.backend.api.dto;

import de.mediasystem.backend.model.Status;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateUserEntryRequestTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void validRequestHasNoViolations() {
        UpdateUserEntryRequest request = new UpdateUserEntryRequest(Status.WATCHING, 8, "Sehr gut bisher.");

        Set<ConstraintViolation<UpdateUserEntryRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void nullStatusIsRejected() {
        UpdateUserEntryRequest request = new UpdateUserEntryRequest(null, 8, "Sehr gut bisher.");

        Set<ConstraintViolation<UpdateUserEntryRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void ratingAboveTenIsRejected() {
        UpdateUserEntryRequest request = new UpdateUserEntryRequest(Status.WATCHING, 11, null);

        Set<ConstraintViolation<UpdateUserEntryRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void ratingBelowOneIsRejected() {
        UpdateUserEntryRequest request = new UpdateUserEntryRequest(Status.WATCHING, 0, null);

        Set<ConstraintViolation<UpdateUserEntryRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void nullRatingAndNoteAreAllowed() {
        UpdateUserEntryRequest request = new UpdateUserEntryRequest(Status.PLANNED, null, null);

        Set<ConstraintViolation<UpdateUserEntryRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }
}
