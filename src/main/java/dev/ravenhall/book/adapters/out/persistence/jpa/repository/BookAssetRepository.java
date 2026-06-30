package dev.ravenhall.book.adapters.out.persistence.jpa.repository;

import dev.ravenhall.book.adapters.out.persistence.jpa.entities.BookAssetJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookAssetRepository extends JpaRepository<BookAssetJpaEntity, UUID> {

    // Spring generates the SQL: SELECT * FROM book_assets WHERE file_checksum = ?
    Optional<BookAssetJpaEntity> findByFileChecksum(String checksum);
}