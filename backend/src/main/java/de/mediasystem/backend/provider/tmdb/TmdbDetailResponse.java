package de.mediasystem.backend.provider.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Antwort von /movie/{id} bzw. /tv/{id}. Beide Endpoints liefern ein flaches Objekt
 * (kein "results"-Array wie bei der Suche) mit denselben Feldnamen wie TmdbSearchResponse.Result,
 * nur ohne "media_type" - welcher Endpoint getroffen wurde, weiß der Aufrufer bereits selbst.
 */
public record TmdbDetailResponse(Long id,
                                  String title,
                                  String overview,
                                  @JsonProperty("release_date") String releaseDate,
                                  @JsonProperty("poster_path") String posterPath,
                                  // ab hier Serie
                                  String name,
                                  @JsonProperty("first_air_date") String firstAirDate
) { }
