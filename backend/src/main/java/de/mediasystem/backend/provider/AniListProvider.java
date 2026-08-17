package de.mediasystem.backend.provider;

import de.mediasystem.backend.model.MediaItem;
import de.mediasystem.backend.model.MediaType;
import de.mediasystem.backend.model.Source;
import de.mediasystem.backend.model.SourceType;
import org.springframework.stereotype.Service;
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



    public AniListProvider(RestClient restclient) {
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

    @Override
    public MediaItem fetchById(String id) {
        return null;
    }

    /**
     * mapps the Result of the GraphQL query to the Items.
     * @param response is the response to the query.
     * @return a list of the items
     */
    public List<MediaItem> mapToMediaItems(AniListSearchResponse response) {
        List<MediaItem> result = new ArrayList<>();
        for (AniListSearchResponse.Media media : response.data().Page().media()) {
            MediaItem item = new MediaItem();
            // map title
            String title = media.title().romaji() != null ? media.title().romaji() : media.title().english();
            item.setTitle(title);
            // map description
            String description = media.description();
            item.setDescription(description);
            // map releaseYear
            Integer year =  media.startDate().year();
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
            // get source Set
            Set<Source> sources =  item.getSources();
            sources.add(source);
            // add to list
            result.add(item);
        }
        return result;
    }
}
