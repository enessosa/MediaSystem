-- V1__init: Initiales Schema für MediaSystem (PostgreSQL).
-- Abgeleitet aus dem DB-Entwurf im Vault. Enums als VARCHAR + CHECK (JPA @Enumerated(STRING)),
-- IDs als IDENTITY, Zeitstempel als TIMESTAMPTZ.

-- Nutzer (Single-Table Inheritance: role = Diskriminator USER/ADMIN)
CREATE TABLE app_user (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(20)  NOT NULL CHECK (role IN ('USER', 'ADMIN')),
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- Globale Konfiguration (u.a. das Signup-Codewort ab dem 11. Nutzer)
CREATE TABLE app_setting (
    setting_key   VARCHAR(100) PRIMARY KEY,
    setting_value TEXT,
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- Medium (nutzerübergreifend, zentral)
CREATE TABLE media_item (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title        VARCHAR(500) NOT NULL,
    description  TEXT,
    release_year INTEGER,
    creator      VARCHAR(255),
    cover_url    VARCHAR(1000),
    media_type   VARCHAR(20)  NOT NULL CHECK (media_type IN ('ANIME', 'MANGA', 'BOOK', 'SERIES'))
);

-- Herkunfts-Quelle(n) eines Mediums; Dedup-Ebene 1 über UNIQUE(source_type, external_id).
-- MANUAL-Items haben external_id = NULL (mehrere sind erlaubt, da NULLs im UNIQUE als verschieden gelten).
CREATE TABLE media_source (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    media_item_id BIGINT       NOT NULL REFERENCES media_item (id) ON DELETE CASCADE,
    source_type   VARCHAR(20)  NOT NULL CHECK (source_type IN ('ANILIST', 'TMDB', 'OPENLIBRARY', 'MANUAL')),
    external_id   VARCHAR(100),
    CONSTRAINT uq_media_source UNIQUE (source_type, external_id),
    CONSTRAINT chk_external_id_present CHECK (source_type = 'MANUAL' OR external_id IS NOT NULL)
);

CREATE INDEX idx_media_source_media_item ON media_source (media_item_id);

-- Persönliche Liste: Verknüpfung User <-> MediaItem; Dedup-Ebene 2 über UNIQUE(user_id, media_item_id).
CREATE TABLE user_entry (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id       BIGINT       NOT NULL REFERENCES app_user (id) ON DELETE CASCADE,
    media_item_id BIGINT       NOT NULL REFERENCES media_item (id) ON DELETE CASCADE,
    status        VARCHAR(20)  NOT NULL CHECK (status IN ('WATCHING', 'COMPLETED', 'PLANNED', 'DROPPED')),
    rating        INTEGER      CHECK (rating BETWEEN 1 AND 10),
    note          TEXT,
    added_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_user_entry UNIQUE (user_id, media_item_id)
);

CREATE INDEX idx_user_entry_user       ON user_entry (user_id);
CREATE INDEX idx_user_entry_media_item ON user_entry (media_item_id);

-- Selbstreferenzielle M:N-Beziehung zwischen Medien (Franchise-Graph, v.a. AniList).
CREATE TABLE media_item_relation (
    source_media_id BIGINT      NOT NULL REFERENCES media_item (id) ON DELETE CASCADE,
    target_media_id BIGINT      NOT NULL REFERENCES media_item (id) ON DELETE CASCADE,
    relation_type   VARCHAR(30) NOT NULL,
    PRIMARY KEY (source_media_id, target_media_id, relation_type),
    CONSTRAINT chk_no_self_relation CHECK (source_media_id <> target_media_id)
);

CREATE INDEX idx_media_item_relation_target ON media_item_relation (target_media_id);