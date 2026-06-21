package desafio.review_jogos.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IgdbGameDto(
        Long id,
        String name,
        String summary,
        @JsonProperty("first_release_date") Long firstReleaseDate,
        Double rating,
        IgdbCoverDto cover,
        List<IgdbPlatformDto> platforms
) {
    public record IgdbCoverDto(String url) {
    }

    public record IgdbPlatformDto(String name) {
    }
}