package dev.ravenhall.book.adapters.out.persistence.jpa;

import dev.ravenhall.book.adapters.out.persistence.jpa.entities.BookAssetJpaEntity;
import dev.ravenhall.book.adapters.out.persistence.jpa.entities.BookJpaEntity;
import dev.ravenhall.book.adapters.out.persistence.jpa.entities.BookSeriesJpaEntity;
import dev.ravenhall.book.adapters.out.persistence.jpa.repository.BookAssetRepository;
import dev.ravenhall.book.adapters.out.persistence.jpa.repository.BookRepository;
import dev.ravenhall.book.adapters.out.persistence.jpa.repository.BookSeriesRepository;
import dev.ravenhall.book.application.ports.out.ManageBookPort;
import dev.ravenhall.book.domain.Book;
import dev.ravenhall.book.domain.BookAsset;
import dev.ravenhall.book.domain.enums.FileFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookPersistenceAdapter implements ManageBookPort {

    private final BookRepository bookRepository;
    private final BookSeriesRepository bookSeriesRepository;
    private final BookAssetRepository bookAssetRepository;

    @Override
    public void storeBook(Book book)
    {
        BookJpaEntity bookEntity = new BookJpaEntity();
        bookEntity.setId(book.getId());
        bookEntity.setTitle(book.getTitle());
        bookEntity.setAuthor(book.getAuthor());
        bookEntity.setAddedAt(book.getAddedAt());

        if(book.getSeriesId() != null)
        {
            BookSeriesJpaEntity seriesProxy = bookSeriesRepository.getReferenceById(book.getSeriesId());
            bookEntity.setSeries(seriesProxy);
        }
        else
        {
            bookEntity.setSeries(null);
        }


        for(BookAsset asset : book.getAssets())
        {
            BookAssetJpaEntity assetEntity = new BookAssetJpaEntity();
            assetEntity.setId(asset.getId());
            assetEntity.setFileChecksum(asset.getChecksum());
            assetEntity.setFilePath(asset.getFilepath());
            assetEntity.setFileFormat(String.valueOf(asset.getFileFormat()));
            assetEntity.setPageCount(asset.getPageCount());
            bookEntity.addAsset(assetEntity);
        }

        bookRepository.save(bookEntity);
    }

    @Override
    public Book retrieveBook(UUID id) {
        BookJpaEntity entity = bookRepository.findById(id).orElse(null);

        if (entity == null) {
            return null;
        }

        UUID seriesId = (entity.getSeries() != null) ? entity.getSeries().getId() : null;

        // constructor 2
        Book book = new Book(
                entity.getId(),
                entity.getTitle(),
                entity.getAddedAt(),
                entity.getAuthor(),
                seriesId
        );

        for (BookAssetJpaEntity assetEntity : entity.getAssets()) {
            BookAsset asset = new BookAsset(
                    assetEntity.getId(),
                    FileFormat.valueOf(assetEntity.getFileFormat()),
                    assetEntity.getFilePath(),
                    assetEntity.getPageCount() != null ? assetEntity.getPageCount() : 0,
                    assetEntity.getFileChecksum(),
                    assetEntity.getFileSize() != null ? assetEntity.getFileSize() : 1L // Default to 1 to pass domain validation if DB is null
            );

            book.addAsset(asset);
        }

        return book;
    }


}
