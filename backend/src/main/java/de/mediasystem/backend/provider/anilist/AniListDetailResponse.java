package de.mediasystem.backend.provider.anilist;

public record AniListDetailResponse(Data data) {

    // Wiederverwendet AniListSearchResponse.Media - gleiche Feldstruktur, hier nur als einzelnes
    // Objekt statt in einer Page/media-Liste ("Media(id: $id)" statt "Page { media(search: ...) }").
    public record Data(AniListSearchResponse.Media Media) { }
}
