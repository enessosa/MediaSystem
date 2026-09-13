package de.mediasystem.backend.provider.anilist;

import de.mediasystem.backend.model.MediaItem;
import de.mediasystem.backend.model.MediaType;
import de.mediasystem.backend.model.Source;
import de.mediasystem.backend.model.SourceType;
import de.mediasystem.backend.provider.Provider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.*;

@Service
public class AniListProvider implements Provider {

    private final RestClient restClient;

    private static final String QUERY = """
            query ($search: String) {
                Page(page: 1, perPage: 10) {
                    media(search: $search, isAdult: false) {
                        id
                        type
                        title {
                            romaji
                            english
                        }
                        description
                        startDate {
                            year
                        }
                        coverImage {
                            large
                        }
                        staff(sort: RELEVANCE, perPage: 5) {
                            edges {
                                role
                                node {
                                    name {
                                        full
                                    }
                                }
                            }
                        }
                    }
                }
            }
            """;



    public AniListProvider(@Qualifier("aniListRestClient") RestClient restclient) {
        this.restClient = restclient;
    }

    @Override
    public List<MediaItem> searchMedia(String search) {
        AniListSearchRequest request = new AniListSearchRequest(QUERY, new AniListSearchRequest.Variables(search));
        AniListSearchResponse response = restClient.post()
                .body(request)
                .retrieve()
                .body(AniListSearchResponse.class);
        return mapToMediaItems(response);
    }

    private static final String DETAIL_QUERY = """
            query ($id: Int) {
                Media(id: $id) {
                    id
                    type
                    title {
                        romaji
                        english
                    }
                    description
                    startDate {
                        year
                    }
                    coverImage {
                        large
                    }
                    staff(sort: RELEVANCE, perPage: 5) {
                        edges {
                            role
                            node {
                                name {
                                    full
                                }
                            }
                        }
                    }
                }
            }
            """;

    @Override
    public MediaItem fetchById(String id) {
        AniListDetailRequest request = new AniListDetailRequest(DETAIL_QUERY,
                new AniListDetailRequest.Variables(Integer.parseInt(id)));
        AniListDetailResponse response;
        try {
            response = restClient.post()
                    .body(request)
                    .retrieve()
                    .body(AniListDetailResponse.class);
        } catch (HttpClientErrorException.NotFound notFound) {
            // AniList antwortet bei unbekannter ID mit echtem HTTP 404 statt data.Media: null.
            return null;
        }

        if (response == null || response.data() == null || response.data().Media() == null) {
            return null;
        }

        return mapMedia(response.data().Media());
    }

    @Override
    public Set<MediaType> supportedMediaTypes() {
        return Set.of(MediaType.ANIME, MediaType.MANGA);
    }

    /**
     * mapps the Result of the GraphQL query to the Items.
     * @param response is the response to the query.
     * @return a list of the items
     */
    public List<MediaItem> mapToMediaItems(AniListSearchResponse response) {
        List<MediaItem> result = new ArrayList<>();
        if (response == null) {
            return result;
        }
        for (AniListSearchResponse.Media media : response.data().Page().media()) {
            result.add(mapMedia(media));
        }
        return result;
    }

    /**
     * mapt ein einzelnes AniList-Media-Objekt auf ein MediaItem - genutzt sowohl von der Suche
     * (mehrere Treffer) als auch von fetchById (ein einzelner Treffer).
     * @param media das rohe AniList-Media-Objekt
     * @return das gemappte MediaItem
     */
    public MediaItem mapMedia(AniListSearchResponse.Media media) {
        MediaItem item = new MediaItem();
        // map title
        String title = media.title().romaji() != null ? media.title().romaji() : media.title().english();
        item.setTitle(title);
        // map description
        String description = media.description();
        item.setDescription(description);
        // map releaseYear
        Integer year = media.startDate().year();
        item.setReleaseYear(year);
        // map type
        MediaType type = MediaType.valueOf(media.type());
        item.setMediaType(type);
        // map creator
        String creator;
        if (media.staff() != null && !media.staff().edges().isEmpty()) {
            creator = media.staff().edges().getFirst().node().name().full();
        } else {
            creator = null;
        }
        item.setCreator(creator);
        // map CoverUrl
        String coverUrl = media.coverImage().large();
        item.setCoverUrl(coverUrl);
        // map source
        Source source = new Source();
        source.setExternalId(media.id().toString());
        source.setMediaItem(item);
        source.setSourceType(SourceType.ANILIST);
        item.getSources().add(source);
        return item;
    }


    @Override
    public SourceType supportedSourceType() {
        return SourceType.ANILIST;
    }
}
