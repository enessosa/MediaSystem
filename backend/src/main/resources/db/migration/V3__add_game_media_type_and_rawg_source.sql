ALTER TABLE media_item DROP CONSTRAINT media_item_media_type_check;
ALTER TABLE media_item ADD CONSTRAINT media_item_media_type_check
    CHECK ( media_type IN ('ANIME', 'MANGA', 'BOOK', 'SERIES', 'MOVIE',
                          'GAME'));

ALTER TABLE media_source DROP CONSTRAINT media_source_source_type_check;
ALTER TABLE media_source ADD CONSTRAINT media_source_source_type_check
    CHECK ( source_type IN ('ANILIST', 'TMDB', 'OPENLIBRARY', 'RAWG', 'MANUAL'));