CREATE SEQUENCE IF NOT EXISTS revinfo_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE book_assets
(
    id                UUID          NOT NULL,
    book_id           UUID          NOT NULL,
    asset_type        VARCHAR(20),
    file_format       VARCHAR(20)   NOT NULL,
    file_path         VARCHAR(1000) NOT NULL,
    file_size_bytes   BIGINT,
    file_checksum     VARCHAR(64)   NOT NULL,
    page_count        INTEGER,
    audio_duration_ms BIGINT,
    ingested_at       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_book_assets PRIMARY KEY (id)
);

CREATE TABLE book_series
(
    id         UUID         NOT NULL,
    name       VARCHAR(500) NOT NULL,
    authors    TEXT[],
    synopsis   TEXT,
    cover_path VARCHAR(500),
    added_at   TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_book_series PRIMARY KEY (id)
);

CREATE TABLE books
(
    id               UUID         NOT NULL,
    series_id        UUID,
    volume_number    DECIMAL(6, 2),
    local_title      VARCHAR(500) NOT NULL,
    local_authors    TEXT[],
    api_title        VARCHAR(500),
    api_authors      TEXT[],
    api_release_year INTEGER,
    api_genres       TEXT[],
    synopsis         TEXT,
    cover_path       VARCHAR(500),
    metadata_status  VARCHAR(255),
    added_at         TIMESTAMP WITHOUT TIME ZONE,
    version          BIGINT,
    CONSTRAINT pk_books PRIMARY KEY (id)
);

CREATE TABLE revchanges
(
    rev        BIGINT NOT NULL,
    entityname VARCHAR(255)
);

CREATE TABLE revinfo
(
    rev      BIGINT NOT NULL,
    revtstmp BIGINT,
    CONSTRAINT pk_revinfo PRIMARY KEY (rev)
);

ALTER TABLE books
    ADD CONSTRAINT FK_BOOKS_ON_SERIES FOREIGN KEY (series_id) REFERENCES book_series (id);

ALTER TABLE book_assets
    ADD CONSTRAINT FK_BOOK_ASSETS_ON_BOOK FOREIGN KEY (book_id) REFERENCES books (id);

ALTER TABLE revchanges
    ADD CONSTRAINT fk_revchanges_on_default_tracking_modified_entities_changelog FOREIGN KEY (rev) REFERENCES revinfo (rev);

CREATE TABLE public.users
(
    id            UUID                     NOT NULL,
    username      VARCHAR(255)             NOT NULL,
    password_hash VARCHAR(255)             NOT NULL,
    display_name  VARCHAR(255),
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    version       BIGINT,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE public.users
    ADD CONSTRAINT uc_users_username UNIQUE (username);

CREATE UNIQUE INDEX idx_books_local_unique
    ON books (lower(local_title), lower((local_authors)[1]), series_id)
    NULLS NOT DISTINCT;

CREATE UNIQUE INDEX idx_book_series_name_unique
    ON book_series (lower(name), lower((authors)[1]));

CREATE UNIQUE INDEX idx_book_assets_checksum_unique
    ON book_assets (book_id, file_checksum);