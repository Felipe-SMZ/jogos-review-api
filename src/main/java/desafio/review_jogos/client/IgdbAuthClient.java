package desafio.review_jogos.client;

import desafio.review_jogos.client.dto.IgdbTokenResponse;
import desafio.review_jogos.config.IgdbProperties;
import desafio.review_jogos.exception.IgdbIntegrationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * Responsável exclusivamente por obter um access_token junto à Twitch,
 * usando o fluxo OAuth2 Client Credentials exigido pela IGDB.
 * <p>
 * Importante: este client NÃO faz cache do token (ainda). A cada chamada,
 * um novo token é solicitado. Cache será adicionado em uma branch futura,
 * já que o token da Twitch dura ~60 dias e pedir um novo a cada requisição
 * é desnecessariamente custoso.
 * <p>
 * Documentação: https://api-docs.igdb.com/#authentication
 */
@Component
public class IgdbAuthClient {

    private final WebClient twitchAuthWebClient;
    private final IgdbProperties igdbProperties;

    public IgdbAuthClient(
            @Qualifier("twitchAuthWebClient") WebClient twitchAuthWebClient,
            IgdbProperties igdbProperties
    ) {
        this.twitchAuthWebClient = twitchAuthWebClient;
        this.igdbProperties = igdbProperties;
    }

    /**
     * Solicita um novo access_token à Twitch.
     *
     * @return o token de acesso (sem o prefixo "Bearer")
     * @throws IgdbIntegrationException se a Twitch retornar erro ou estiver indisponível
     */
    public String obterAccessToken() {
        try {
            IgdbTokenResponse response = twitchAuthWebClient
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/oauth2/token")
                            .queryParam("client_id", igdbProperties.clientId())
                            .queryParam("client_secret", igdbProperties.clientSecret())
                            .queryParam("grant_type", "client_credentials")
                            .build())
                    .retrieve()
                    .bodyToMono(IgdbTokenResponse.class)
                    .block();

            if (response == null || response.accessToken() == null) {
                throw new IgdbIntegrationException(
                        "A Twitch retornou uma resposta vazia ao solicitar o access_token.");
            }

            return response.accessToken();

        } catch (WebClientResponseException e) {
            throw new IgdbIntegrationException(
                    "Falha ao autenticar na Twitch (status %d): %s"
                            .formatted(e.getStatusCode().value(), e.getMessage()),
                    e
            );
        } catch (Exception e) {
            throw new IgdbIntegrationException(
                    "Erro inesperado ao tentar obter o access_token da Twitch.", e
            );
        }
    }
}