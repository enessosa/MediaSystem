package de.mediasystem.backend.api.dto;

import de.mediasystem.backend.model.MediaType;
import de.mediasystem.backend.model.SourceType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AddToListRequestTest {

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
    void validExternalRequestHasNoViolations() {
        AddToListRequest request = new AddToListRequest(
                "Naruto", "Beschreibung", 1999, MediaType.MANGA,
                "Masashi Kishimoto", "https://example.org/cover.jpg", SourceType.ANILIST, "1");

        Set<ConstraintViolation<AddToListRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void validManualRequestWithoutExternalIdHasNoViolations() {
        AddToListRequest request = new AddToListRequest(
                "Eigener Eintrag", null, null, MediaType.BOOK,
                null, null, SourceType.MANUAL, null);

        Set<ConstraintViolation<AddToListRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void blankTitleIsRejected() {
        AddToListRequest request = new AddToListRequest(
                "", "Beschreibung", 1999, MediaType.MANGA,
                null, null, SourceType.ANILIST, "1");

        Set<ConstraintViolation<AddToListRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void nullMediaTypeIsRejected() {
        AddToListRequest request = new AddToListRequest(
                "Naruto", "Beschreibung", 1999, null,
                null, null, SourceType.ANILIST, "1");

        Set<ConstraintViolation<AddToListRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void nullSourceTypeIsRejected() {
        AddToListRequest request = new AddToListRequest(
                "Naruto", "Beschreibung", 1999, MediaType.MANGA,
                null, null, null, "1");

        Set<ConstraintViolation<AddToListRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void missingExternalIdForNonManualSourceIsRejected() {
        AddToListRequest request = new AddToListRequest(
                "Naruto", "Beschreibung", 1999, MediaType.MANGA,
                null, null, SourceType.ANILIST, null);

        Set<ConstraintViolation<AddToListRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void blankExternalIdForNonManualSourceIsRejected() {
        AddToListRequest request = new AddToListRequest(
                "Naruto", "Beschreibung", 1999, MediaType.MANGA,
                null, null, SourceType.ANILIST, "   ");

        Set<ConstraintViolation<AddToListRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
    }
}
