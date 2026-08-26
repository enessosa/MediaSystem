-- MediaType um MOVIE erweitert (TmdbProvider deckt Filme und Serien ab, siehe ADR-002/Provider-Pattern).
-- Postgres kennt kein ALTER auf CHECK-Constraints, daher drop + recreate mit erweiterter Werteliste.
ALTER TABLE media_item DROP CONSTRAINT media_item_media_type_check;
ALTER TABLE media_item ADD CONSTRAINT media_item_media_type_check
    CHECK (media_type IN ('ANIME', 'MANGA', 'BOOK', 'SERIES', 'MOVIE'));