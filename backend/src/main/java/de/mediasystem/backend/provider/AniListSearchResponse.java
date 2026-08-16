package de.mediasystem.backend.provider;

import java.util.List;

public record AniListSearchResponse(Data data) {

    // Staff
    public record StaffName(String full) { }

    public record StaffNode(StaffName name) { }

    public record StaffEdge(String role,  StaffNode node) { }

    public record Staff(List<StaffEdge> edges) { }


    // title
    public record Title(String romaji, String english) { }

    //Date
    public record StartDate(Integer year) { }

    //Cover
    public record CoverImage(String large) { }

    //media
    public record Media(String type,
                        String description,
                        Long id,
                        Title title,
                        StartDate startDate,
                        CoverImage coverImage,
                        Staff staff) { }

    public record Page(List<Media> media) { }

    public record Data(Page Page) { }
}
