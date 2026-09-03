package dev.ravenhall.book.adapters.out.metadata.hardcover;

import dev.ravenhall.book.adapters.out.metadata.hardcover.dto.HardcoverDTO;
import dev.ravenhall.book.adapters.out.metadata.hardcover.mapper.HardcoverMetadataMapper;
import dev.ravenhall.book.application.ports.out.data.metadata.api.ApiMetadataResponse;
import dev.ravenhall.book.application.ports.out.data.metadata.api.ApiMetadataSearchRequest;
import dev.ravenhall.book.application.ports.out.metadata.FetchBookMetadataPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.graphql.client.HttpSyncGraphQlClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.LockSupport;

@Slf4j
@Component
@ConditionalOnProperty(name = "metadata.provider", havingValue = "hardcover")
public class HardcoverAdapter implements FetchBookMetadataPort {

    private final HttpSyncGraphQlClient graphQlClient;
    private final HardcoverMetadataMapper mapper;

    public HardcoverAdapter(
            @Value("${metadata.hardcover.apikey}") String apiKey,
            @Value("${metadata.hardcover.url}") String url,
            HardcoverMetadataMapper mapper) {

        this.mapper = mapper;

        RestClient restClient = RestClient.builder()
                .baseUrl(url)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();

        this.graphQlClient = HttpSyncGraphQlClient.builder(restClient).build();
    }

    @Override
    public Map<ApiMetadataSearchRequest, ApiMetadataResponse> fetchBookMetadata(List<ApiMetadataSearchRequest> requests) {

        Map<ApiMetadataSearchRequest, ApiMetadataResponse> results = new HashMap<>();

        for (ApiMetadataSearchRequest request : requests) {
            HardcoverDTO dto;

            log.info("Fetching Hardcover metadata for request {}", request.toString());
            try {
                if (!request.authors().isEmpty()) {
                    dto = graphQlClient.documentName("hardcover")
                            .operationName("BooksWithAuthor")
                            .variable("title", request.title() + " by " + String.join(" ", request.authors()))
                            .variable("authors", "author_names:" + request.authors()) // implicitly calls .toString()
                            .retrieveSync("search.results")
                            .toEntity(HardcoverDTO.class);
                } else {
                    dto = graphQlClient.documentName("hardcover")
                            .operationName("BooksNoAuthor")
                            .variable("title", request.title())
                            .retrieveSync("search.results")
                            .toEntity(HardcoverDTO.class);
                }

                // a simple scoring system
                if (dto != null && dto.hits() != null && !dto.hits().isEmpty()) {
                    var hit = dto.hits().stream()
                            .filter(h -> request.title().equalsIgnoreCase(h.document().title()))
                            .findFirst()
                            .orElse(dto.hits().getFirst());

                    results.put(request, mapper.toDomain(hit.document()));
                } else {
                    log.warn("Hardcover returned no results for request: {}", request);
                }
            } catch (Exception e) {
                log.error("Failed to fetch metadata from Hardcover for request: {}. Reason: {}", request, e.getMessage(), e);
            }
            LockSupport.parkNanos(1_100_000_000L); // ~1.1s pause, no checked exception
        }

        return results;
    }
}