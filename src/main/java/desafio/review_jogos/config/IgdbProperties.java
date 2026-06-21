package desafio.review_jogos.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propriedades de configuração da integração com a IGDB/Twitch.
 *
 * Lidas a partir do application.properties:
 * igdb.client-id=...
 * igdb.client-secret=...
 */
@ConfigurationProperties(prefix = "igdb")
public record IgdbProperties(
        String clientId,
        String clientSecret
) {
}