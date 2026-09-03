package dev.ravenhall.book.application.ports.out.metadata;

import dev.ravenhall.book.application.ports.out.data.metadata.api.ApiMetadataResponse;
import dev.ravenhall.book.application.ports.out.data.metadata.api.ApiMetadataSearchRequest;

import java.util.List;
import java.util.Map;

public interface FetchBookMetadataPort {

    Map<ApiMetadataSearchRequest, ApiMetadataResponse> fetchBookMetadata(List<ApiMetadataSearchRequest> requests);

}
