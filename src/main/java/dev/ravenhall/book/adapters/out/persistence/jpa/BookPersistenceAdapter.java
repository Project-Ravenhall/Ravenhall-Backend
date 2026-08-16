package dev.ravenhall.book.adapters.out.persistence.jpa;

import dev.ravenhall.book.adapters.out.persistence.jpa.entities.BookAssetJpaEntity;
import dev.ravenhall.book.adapters.out.persistence.jpa.entities.BookJpaEntity;
import dev.ravenhall.book.adapters.out.persistence.jpa.repository.BookRepository;
import dev.ravenhall.book.application.ports.out.ManageBookPort;
import dev.ravenhall.book.domain.Book;
import dev.ravenhall.book.domain.BookAsset;
import dev.ravenhall.book.domain.enums.FileFormat;
import dev.ravenhall.book.domain.enums.MetadataStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookPersistenceAdapter implements ManageBookPort {

    private final BookRepository bookRepository;
    private final dev.ravenhall.book.adapters.out.persistence.jpa.repository.BookAssetRepository bookAssetRepository;
    private final EntityManager entityManager;

    @Override
    public void storeBook(Book book) {

        // I used ON CONFLICT ON CONSTRAINT so we don't have to duplicate the lower() syntax here.
        String upsertSql = """
            INSERT INTO books (id, local_title, local_authors, series_id, added_at, metadata_status, api_title, api_authors, api_release_year, api_genres, synopsis, cover_path)
            VALUES (:id, :localTitle, cast(:localAuthors as text[]), :seriesId, :addedAt, :metadataStatus, :apiTitle, cast(:apiAuthors as text[]), :apiReleaseYear, cast(:apiGenres as text[]), :synopsis, :coverPath)
            ON CONFLICT (lower(local_title), lower((local_authors)[1]), series_id)
            DO UPDATE SET local_title = EXCLUDED.local_title
            RETURNING id
        """;

        Query query = entityManager.createNativeQuery(upsertSql)
                .setParameter("id", book.getId())
                .setParameter("localTitle", book.getLocalTitle())
                .setParameter("localAuthors", book.getLocalAuthors() != null ? book.getLocalAuthors().toArray(new String[0]) : new String[0])
                .setParameter("seriesId", book.getSeriesId())
                .setParameter("addedAt", book.getAddedAt())
                .setParameter("metadataStatus", book.getMetadataStatus() != null ? book.getMetadataStatus().name() : null)
                .setParameter("apiTitle", book.getApiTitle())
                .setParameter("apiAuthors", book.getApiAuthors() != null ? book.getApiAuthors().toArray(new String[0]) : null)
                .setParameter("apiReleaseYear", book.getApiReleaseYear())
                .setParameter("apiGenres", book.getApiGenres() != null ? book.getApiGenres().toArray(new String[0]) : null)
                .setParameter("synopsis", book.getSynopsis())
                .setParameter("coverPath", book.getCoverPath());

        UUID resolvedBookId = (UUID) query.getSingleResult(); // it returns an object so we cast to UUID

        for (BookAsset asset : book.getAssets()) {
            entityManager.createNativeQuery("""
        INSERT INTO book_assets (id, book_id, file_format, file_path, file_checksum, page_count, ingested_at, file_size_bytes)
        VALUES (:id, :bookId, :fileFormat, :filePath, :fileChecksum, :pageCount, now(), :fileSize)
        ON CONFLICT (book_id, file_checksum) DO NOTHING
        """)
                    .setParameter("id", asset.getId())
                    .setParameter("bookId", resolvedBookId)
                    .setParameter("fileFormat", String.valueOf(asset.getFileFormat()))
                    .setParameter("filePath", asset.getFilepath())
                    .setParameter("fileChecksum", asset.getChecksum())
                    .setParameter("pageCount", asset.getPageCount())
                    .setParameter("fileSize", asset.getFileSize())
                    .executeUpdate();
        }
    }

    @Override
    public UUID upsertSeries(String seriesName, List<String> authors) {
        String sql = """
            INSERT INTO book_series (id, name, authors)
            VALUES (:id, :name, cast(:authors as text[]))
            ON CONFLICT (lower(name), lower((authors)[1]))
            DO UPDATE SET name = EXCLUDED.name
            RETURNING id
            """;

        return (UUID) entityManager.createNativeQuery(sql)
                .setParameter("id", UUID.randomUUID())
                .setParameter("name", seriesName)
                .setParameter("authors", authors != null ? authors.toArray(new String[0]) : new String[0])
                .getSingleResult();
    }

    @Override
    public Book retrieveBook(UUID id) {
        BookJpaEntity entity = bookRepository.findById(id).orElse(null);

        if (entity == null) {
            return null;
        }

        UUID seriesId = (entity.getSeries() != null) ? entity.getSeries().getId() : null;

        Book book = new Book(
                entity.getId(),
                entity.getLocalTitle(),
                entity.getVersion(),
                entity.getLocalAuthors(),
                entity.getSynopsis(),
                entity.getCoverPath(),
                seriesId,
                entity.getAddedAt(),
                entity.getApiTitle(),
                entity.getApiAuthors(),
                entity.getMetadataStatus(),
                entity.getApiReleaseYear(),
                entity.getApiGenres()
        );

        for (BookAssetJpaEntity assetEntity : entity.getAssets()) {
            BookAsset asset = new BookAsset(
                    assetEntity.getId(),
                    FileFormat.valueOf(assetEntity.getFileFormat()),
                    assetEntity.getFilePath(),
                    assetEntity.getPageCount() != null ? assetEntity.getPageCount() : 0,
                    assetEntity.getFileChecksum(),
                    assetEntity.getFileSize() != null ? assetEntity.getFileSize() : 1L
            );
            book.addAsset(asset);
        }

        return book;
    }

    @Override
    public List<Book> retrieveAllBooks() {
        List<BookJpaEntity> bookEntityList = bookRepository.findAll();

        List<Book> books = new ArrayList<>();
        for (BookJpaEntity entity : bookEntityList) {
            UUID seriesId = (entity.getSeries() != null) ? entity.getSeries().getId() : null;
            Book book = new Book(
                    entity.getId(),
                    entity.getLocalTitle(),
                    entity.getVersion(),
                    entity.getLocalAuthors(),
                    entity.getSynopsis(),
                    entity.getCoverPath(),
                    seriesId,
                    entity.getAddedAt(),
                    entity.getApiTitle(),
                    entity.getApiAuthors(),
                    entity.getMetadataStatus(),
                    entity.getApiReleaseYear(),
                    entity.getApiGenres()
            );
            for (BookAssetJpaEntity assetEntity : entity.getAssets()) {
                book.addAsset(new BookAsset(
                        assetEntity.getId(),
                        FileFormat.valueOf(assetEntity.getFileFormat()),
                        assetEntity.getFilePath(),
                        assetEntity.getPageCount() != null ? assetEntity.getPageCount() : 0,
                        assetEntity.getFileChecksum(),
                        assetEntity.getFileSize() != null ? assetEntity.getFileSize() : 1L
                ));
            }
            books.add(book);
        }
        return books;
    }

    @Override
    public void updateBookMetadata(List<Book> books) {
        for (Book book : books) {
            bookRepository.updateBookMetadata(
                    book.getId(),
                    book.getApiTitle(),
                    book.getApiAuthors(),
                    book.getApiReleaseYear(),
                    book.getApiGenres(),
                    book.getSynopsis(),
                    book.getCoverPath(),
                    book.getMetadataStatus()
            );
        }
    }

    @Override
    public List<Book> retrieveTop25BookMetadataStatus(List<MetadataStatus> statuses) {
        List<BookJpaEntity> entities = bookRepository.findTop25ByMetadataStatusIn(statuses);

        List<Book> books = new ArrayList<>();
        for (BookJpaEntity entity : entities) {
            UUID seriesId = (entity.getSeries() != null) ? entity.getSeries().getId() : null;
            Book book = new Book(
                    entity.getId(),
                    entity.getLocalTitle(),
                    entity.getVersion(),
                    entity.getLocalAuthors(),
                    entity.getSynopsis(),
                    entity.getCoverPath(),
                    seriesId,
                    entity.getAddedAt(),
                    entity.getApiTitle(),
                    entity.getApiAuthors(),
                    entity.getMetadataStatus(),
                    entity.getApiReleaseYear(),
                    entity.getApiGenres()
            );

            books.add(book);
        }
        return books;
    }

    @Override
    public BookAsset retrieveAsset(UUID assetId) {
        BookAssetJpaEntity assetEntity = bookAssetRepository.findById(assetId).orElse(null);
        if (assetEntity == null) {
            return null;
        }
        return new BookAsset(
                assetEntity.getId(),
                FileFormat.valueOf(assetEntity.getFileFormat()),
                assetEntity.getFilePath(),
                assetEntity.getPageCount() != null ? assetEntity.getPageCount() : 0,
                assetEntity.getFileChecksum(),
                assetEntity.getFileSize() != null ? assetEntity.getFileSize() : 1L
        );
    }
}