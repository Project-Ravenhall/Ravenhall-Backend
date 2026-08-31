package dev.ravenhall.book.adapters.out.persistence.jpa.entities.progress;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class BookProgressId implements Serializable {

    private UUID user;
    private UUID asset;
}
