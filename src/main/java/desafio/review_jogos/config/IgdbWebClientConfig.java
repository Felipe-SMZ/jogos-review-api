package desafio.review_jogos.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuração dos clientes WebClient usados na integração com a IGDB.
 * <p>
 * Dois beans distintos, pois são duas APIs com base URLs diferentes:
 * - Twitch (autenticação OAuth2): https://id.twitch.tv
 * - IGDB (dados de jogos): https://api.igdb.com/v4
 */
@Configuration
@EnableConfigurationProperties(IgdbProperties.class)
public class IgdbWebClientConfig {

    @Bean
    public WebClient twitchAuthWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://id.twitch.tv")
                .build();
    }

    @Bean
    public WebClient igdbWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://api.igdb.com/v4")
                .build();
    }
}