package de.mediasystem.backend.provider.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * tmdb detail response.
 * @param id id
 * @param title title
 * @param overview overview
 * @param releaseDate release Date
 * @param posterPath poster path
 * @param name name
 * @param firstAirDate first air date
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
