package dev.ravenhall.book.adapters.out.metadata.google;

import dev.ravenhall.book.adapters.out.metadata.google.dto.GoogleBooksResponseDTO;
import dev.ravenhall.book.adapters.out.metadata.google.mapper.GoogleBooksResponseMapper;
import dev.ravenhall.book.application.ports.out.data.metadata.api.ApiMetadataResponse;
import dev.ravenhall.book.application.ports.out.data.metadata.api.ApiMetadataSearchRequest;
import dev.ravenhall.book.application.ports.out.metadata.FetchBookMetadataPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(name = "metadata.provider", havingValue = "google", matchIfMissing = true)
public class GoogleBooksAdapter implements FetchBookMetadataPort {

    private final String apiKey;
    private final String url;
    private final RestClient restClient;
    private final GoogleBooksResponseMapper mapper;

    public GoogleBooksAdapter(
            @Value("${metadata.google.apikey}") String apiKey,
            @Value("${metadata.google.url}") String url,
            RestClient restClient,
            GoogleBooksResponseMapper mapper) {

        this.apiKey = apiKey;
        this.url = url;
        this.restClient = restClient;
        this.mapper = mapper;
    }

    @Override
    @Retryable(backoff = @Backoff(delay = 5000))
    public Map<ApiMetadataSearchRequest, ApiMetadataResponse> fetchBookMetadata(List<ApiMetadataSearchRequest> requests) {

        Map<ApiMetadataSearchRequest, ApiMetadataResponse> results = new HashMap<>();

        for (ApiMetadataSearchRequest request : requests) {

            log.info("Fetching Google Books metadata for request {}", request.toString());
            UriBuilder uriBuilder = UriComponentsBuilder.fromUriString(url);

            URI uri = uriBuilder
                    .queryParam("q", request.title())
                    .queryParam("key", apiKey)
                    .queryParam("maxResults", 10)
                    .queryParam("orderBy", "relevance")
                    .build();

            try {
                GoogleBooksResponseDTO dto = restClient.get()
                        .uri(uri)
                        .retrieve()
                        .body(GoogleBooksResponseDTO.class);

                if (dto != null) {
                    results.put(request, mapper.DTOtoDomain(dto));
                } else {
                    log.warn("Google Books returned empty response for request: {}", request);
                }
            } catch (Exception e) {
                log.error("Failed to fetch metadata from Google Books for request: {}. Reason: {}", request, e.getMessage(), e);
            }
        }

        return results;
    }
}