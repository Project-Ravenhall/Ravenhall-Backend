package dev.ravenhall.book.adapters.out.persistence.jpa.repository;

import dev.ravenhall.book.adapters.out.persistence.jpa.entities.BookSeriesJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookSeriesRepository extends JpaRepository<BookSeriesJpaEntity, UUID> {
}
