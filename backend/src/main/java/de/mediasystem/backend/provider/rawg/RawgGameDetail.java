package de.mediasystem.backend.provider.rawg;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Antwort von /games/{id} - anders als die Suchliste liefert der Detail-Call eine echte Beschreibung. */
public record RawgGameDetail(Long id,
                              String name,
                              String released,
                              @JsonProperty("background_image") String backgroundImage,
                              @JsonProperty("description_raw") String descriptionRaw
) { }
