package dev.ravenhall.book.adapters.in.web.mapper;

import dev.ravenhall.book.adapters.in.web.dto.ProgressDTO;
import dev.ravenhall.book.domain.Progress;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProgressMapper {

    ProgressDTO domainToDto(Progress progress);
}
