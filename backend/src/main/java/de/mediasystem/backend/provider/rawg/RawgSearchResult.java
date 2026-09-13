package de.mediasystem.backend.provider.rawg;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record RawgSearchResult(List<Result> results) {

    public record Result(Long id,
                          String name,
                          String released,
                          @JsonProperty("background_image") String backgroundImage
    ) { }
}
