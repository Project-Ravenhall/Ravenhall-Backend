package dev.ravenhall.book.adapters.out.persistence.jpa.repository;

import dev.ravenhall.book.adapters.out.persistence.jpa.entities.BookSeriesJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookSeriesRepository extends JpaRepository<BookSeriesJpaEntity, UUID> {
}
