package de.mediasystem.backend.provider.rawg;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Antwort von /games/{id} - anders als die Suchliste liefert der Detail-Call eine echte Beschreibung.
 * @param id id
 * @param name name
 * @param released released
 * @param backgroundImage image
 * @param descriptionRaw description
 */
public record RawgGameDetail(Long id,
                              String name,
                              String released,
                              @JsonProperty("background_image") String backgroundImage,
                              @JsonProperty("description_raw") String descriptionRaw
) { }
