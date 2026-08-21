package de.mediasystem.backend.service;

import de.mediasystem.backend.api.dto.MediaSearchResult;
import de.mediasystem.backend.api.dto.UserEntryResponse;
import de.mediasystem.backend.db.MediaItemRepository;
import de.mediasystem.backend.db.SourceRepository;
import de.mediasystem.backend.db.UserEntryRepository;
import de.mediasystem.backend.db.UserRepository;
import de.mediasystem.backend.model.MediaItem;
import de.mediasystem.backend.model.Source;
import de.mediasystem.backend.model.Status;
import de.mediasystem.backend.model.UserEntry;
import de.mediasystem.backend.model.roles.User;
import de.mediasystem.backend.provider.Provider;
import de.mediasystem.backend.service.exception.AlreadyInListException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MediaService {

    private final Provider provider;
    private final MediaItemRepository mediaItemRepository;
    private final SourceRepository sourceRepository;
    private final UserEntryRepository userEntryRepository;
    private final UserRepository userRepository;


    public MediaService(Provider provider,
                        MediaItemRepository mediaItemRepository,
                        SourceRepository sourceRepository,
                        UserEntryRepository userEntryRepository,
                        UserRepository userRepository) {
        this.provider = provider;
        this.mediaItemRepository = mediaItemRepository;
        this.sourceRepository = sourceRepository;
        this.userEntryRepository = userEntryRepository;
        this.userRepository = userRepository;
    }

    /**
     * this method converts search results into a record class to prepare the data that is send to the frontend.
     * @param query is the query for the search
     * @return the converted Record class wrapped  in a list.
     */
    public List<MediaSearchResult> searchMedia(String query) {
        List<MediaItem> results = provider.searchMedia(query);
        List<MediaSearchResult> converted = new ArrayList<>();
        for (MediaItem item : results) {
            Source source = item.getSources().iterator().next();
            MediaSearchResult searchResult = new MediaSearchResult(
                    item.getTitle(),
                    item.getDescription(),
                    item.getReleaseYear(),
                    item.getMediaType(),
                    item.getCreator(),
                    item.getCoverUrl(),
                    source.getSourceType(),
                    source.getExternalId()
            );
            converted.add(searchResult);
        }
        return converted;
    }

    /**
     * fügt ein MediaItem zu der liste eines Users hinzu.
     * @param result suchergebnis
     * @param userId userid
     * @return userEntry
     */
    @Transactional
    public UserEntryResponse addToUserList(MediaSearchResult result, Long userId) {
        Optional<Source> optionalSource =
                sourceRepository.findBySourceTypeAndExternalId(result.sourceType(), result.externalId());

        MediaItem item;
        if (optionalSource.isPresent()) {
            item = optionalSource.get().getMediaItem();
        } else {
            item = mapToNewMediaItem(result);
            mediaItemRepository.save(item);
        }

        if (userEntryRepository.existsByUserIdAndMediaItemId(userId, item.getId())) {
            throw new AlreadyInListException("Medium ist bereits in der Liste.");
        }

        User user = userRepository.findById(userId).orElseThrow();

        UserEntry userEntry = new UserEntry();
        userEntry.setUser(user);
        userEntry.setMediaItem(item);
        userEntry.setStatus(Status.PLANNED);
        userEntryRepository.save(userEntry);

        return mapUserEntrytoResponse(userEntry);
    }

    private MediaItem mapToNewMediaItem(MediaSearchResult result) {
        MediaItem item = new MediaItem();
        item.setTitle(result.title());
        item.setDescription(result.description());
        item.setReleaseYear(result.releaseYear());
        item.setMediaType(result.mediaType());
        item.setCoverUrl(result.coverUrl());
        item.setCreator(result.creator());

        Source source = new Source();
        source.setSourceType(result.sourceType());
        source.setExternalId(result.externalId());
        source.setMediaItem(item);
        item.getSources().add(source);

        return item;
    }

    private UserEntryResponse mapUserEntrytoResponse(UserEntry userEntry) {
        MediaItem item = userEntry.getMediaItem();
        return new UserEntryResponse(
                userEntry.getId(),
                userEntry.getStatus(),
                userEntry.getRating(),
                userEntry.getNote(),
                userEntry.getAddedAt(),
                item.getId(),
                item.getTitle(),
                item.getDescription(),
                item.getReleaseYear(),
                item.getMediaType(),
                item.getCreator(),
                item.getCoverUrl()
        );
    }
}
