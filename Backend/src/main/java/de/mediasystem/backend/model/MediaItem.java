package de.mediasystem.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedList;

@Getter
@AllArgsConstructor
public class MediaItem {

    private Long id;
    private String title;
    private int releaseYear;
    private String creator;
    private String description;
    private LinkedList<String> relations;
    private MediaType mediaType;
    private String coverUrl;
    private Source source;
}
