package de.mediasystem.backend.provider.anilist;

public record AniListDetailRequest(String query, Variables variables) {

    public record Variables(Integer id) { }
}
