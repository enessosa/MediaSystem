package de.mediasystem.backend.provider.openlibrary;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OpenLibrarySearchResponse(List<Doc> docs) {

    public record Doc(String key,
                       String title,
                       @JsonProperty("author_name") List<String> authorName,
                       @JsonProperty("first_publish_year") Integer firstPublishYear,
                       @JsonProperty("cover_i") Long coverId
    ) { }
}
