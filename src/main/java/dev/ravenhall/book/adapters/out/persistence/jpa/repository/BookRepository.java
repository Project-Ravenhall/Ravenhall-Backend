package dev.ravenhall.book.adapters.out.persistence.jpa.repository;

import dev.ravenhall.book.adapters.out.persistence.jpa.entities.BookJpaEntity;
import dev.ravenhall.book.domain.enums.MetadataStatus;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<BookJpaEntity, UUID> {

    @EntityGraph(attributePaths = {"assets"}) // pulls in assets with books instead of lazy loading them
    @NullMarked
    Optional<BookJpaEntity> findById(UUID id, UUID userId);

    @EntityGraph(attributePaths = {"assets"})
    @Query("SELECT b FROM BookJpaEntity b")
    @NullMarked
    List<BookJpaEntity> findAll();

    @EntityGraph(attributePaths = {"series"})
    List<BookJpaEntity> findTop25ByMetadataStatusIn(List<MetadataStatus> statuses);

    @Modifying
    @Query("""
        UPDATE BookJpaEntity b
        SET b.apiTitle = :apiTitle,
            b.apiAuthors = :apiAuthors,
            b.apiReleaseYear = :apiReleaseYear,
            b.apiGenres = :apiGenres,
            b.synopsis = :synopsis,
            b.coverPath = :coverPath,
            b.metadataStatus = :status
        WHERE b.id = :id
    """)
    void updateBookMetadata(@Param("id") UUID id, 
                            @Param("apiTitle") String apiTitle, 
                            @Param("apiAuthors") List<String> apiAuthors, 
                            @Param("apiReleaseYear") Integer apiReleaseYear, 
                            @Param("apiGenres") List<String> apiGenres, 
                            @Param("synopsis") String synopsis, 
                            @Param("coverPath") String coverPath, 
                            @Param("status") MetadataStatus status);
}
