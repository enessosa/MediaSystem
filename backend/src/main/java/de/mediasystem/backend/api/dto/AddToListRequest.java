package de.mediasystem.backend.api.dto;

import de.mediasystem.backend.model.MediaType;
import de.mediasystem.backend.model.SourceType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddToListRequest(
        @NotBlank String title,
        String description,
        Integer releaseYear,
        @NotNull MediaType mediaType,
        String creator,
        String coverUrl,
        @NotNull SourceType sourceType,
        String externalId) {

    /**
     * externalId ist nur für SourceType.MANUAL optional (kein externes Original zum Referenzieren) -
     * bei jedem anderen SourceType muss es vorhanden sein, sonst könnte weder dedupliziert noch später
     * per fetchById nachgeschlagen werden. Spiegelt den DB-Constraint chk_external_id_present.
     * @return true, wenn die Kombination aus sourceType und externalId gültig ist
     */
    @AssertTrue(message = "externalId darf nur bei SourceType.MANUAL fehlen")
    public boolean isExternalIdPresentUnlessManual() {
        return sourceType == null || sourceType == SourceType.MANUAL
                || (externalId != null && !externalId.isBlank());
    }
}
