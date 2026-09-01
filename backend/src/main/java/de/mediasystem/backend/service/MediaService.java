package de.mediasystem.backend.service;

import de.mediasystem.backend.api.dto.MediaSearchResult;
import de.mediasystem.backend.api.dto.UpdateUserEntryRequest;
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
import de.mediasystem.backend.service.exception.UserEntryNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MediaService {

    private final List<Provider> providers;
    private final MediaItemRepository mediaItemRepository;
    private final SourceRepository sourceRepository;
    private final UserEntryRepository userEntryRepository;
    private final UserRepository userRepository;


    public MediaService(List<Provider> providers,
                        MediaItemRepository mediaItemRepository,
                        SourceRepository sourceRepository,
                        UserEntryRepository userEntryRepository,
                        UserRepository userRepository) {
        this.providers = providers;
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
        List<MediaItem> results = new ArrayList<>();
        for (Provider provider : providers) {
            results.addAll(provider.searchMedia(query));
        }
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

    /**
     * Holt von dem Repo die User Entries als liste und mappt sie dann stück für stück als UserEntryRepsonse.
     * @param userId ist die user id
     * @return die response liste.
     */
    @Transactional
    public List<UserEntryResponse> getUserEntries(Long userId) {
        List<UserEntry> userEntries = userEntryRepository.findByUserId(userId);
        List<UserEntryResponse> userEntryResponses = new ArrayList<>();
        for (UserEntry userEntry : userEntries) {
            userEntryResponses.add(mapUserEntrytoResponse(userEntry));
        }
        return userEntryResponses;
    }

    /**
     * this method updates the rating, status and note of a UserEntry.
     * @param entryId the entry id
     * @param userId user id
     * @param request the new information
     * @return returns a UserEntryResponse record
     */
    @Transactional
    public UserEntryResponse updateUserEntry(Long entryId, Long userId,
                                              UpdateUserEntryRequest request) {
        UserEntry userEntry = userEntryRepository.findById(entryId)
                .orElseThrow(UserEntryNotFoundException::new);

        if (!userEntry.getUser().getId().equals(userId)) {
            throw new UserEntryNotFoundException();
        }

        userEntry.updateStatus(request.status());
        userEntry.updateRating(request.rating());
        userEntry.setNote(request.note());
        userEntryRepository.save(userEntry);

        return mapUserEntrytoResponse(userEntry);
    }

    /**
     * method to delete userEntry.
     * @param entryId entry
     * @param userId user
     */
    @Transactional
    public void deleteUserEntry(Long entryId, Long userId) {
        UserEntry userEntry = userEntryRepository.findById(entryId)
                .orElseThrow(UserEntryNotFoundException::new);

        if (!userEntry.getUser().getId().equals(userId)) {
            throw new UserEntryNotFoundException();
        }

        userEntryRepository.delete(userEntry);
    }
}
