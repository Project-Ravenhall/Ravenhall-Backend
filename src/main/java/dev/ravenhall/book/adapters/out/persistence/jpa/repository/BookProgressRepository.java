package dev.ravenhall.book.adapters.out.persistence.jpa.repository;

import dev.ravenhall.book.adapters.out.persistence.jpa.entities.progress.BookProgressEntity;
import dev.ravenhall.book.adapters.out.persistence.jpa.entities.progress.BookProgressId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookProgressRepository extends JpaRepository<BookProgressEntity, BookProgressId> {

    List<BookProgressEntity> findByUser_IdAndAsset_IdIn(UUID userId, List<UUID> assetIds);


    @Query(value = """
        INSERT INTO book_progress (user_id, asset_id, synced_page, version)
        VALUES (:userId, :assetId, :syncedPage, 0)
        ON CONFLICT (user_id, asset_id)
        DO UPDATE SET
            synced_page = EXCLUDED.synced_page,
            version = book_progress.version + 1
        WHERE book_progress.version = :version
        RETURNING version
    """, nativeQuery = true)
    Optional<Integer> upsertProgress(@Param("userId") UUID userId,
                            @Param("assetId") UUID assetId,
                            @Param("syncedPage") int syncedPage,
                            @Param("version") int version);
}
