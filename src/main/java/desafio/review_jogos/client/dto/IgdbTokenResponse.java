package desafio.review_jogos.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representa a resposta do endpoint de autenticação OAuth2 da Twitch
 * (POST https://id.twitch.tv/oauth2/token).
 *
 * Exemplo de resposta:
 * {
 *   "access_token": "abc123...",
 *   "expires_in": 5587808,
 *   "token_type": "bearer"
 * }
 *
 * Os nomes dos campos no JSON vêm em snake_case; mapeamos explicitamente
 * com @JsonProperty para não depender de configuração global do Jackson.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IgdbTokenResponse(

        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("expires_in")
        Long expiresIn,

        @JsonProperty("token_type")
        String tokenType
) {
}