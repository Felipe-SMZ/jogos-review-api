package desafio.review_jogos.client;

import desafio.review_jogos.client.dto.IgdbGameDto;
import desafio.review_jogos.config.IgdbProperties;
import desafio.review_jogos.exception.IgdbIntegrationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

/**
 * Responsável por buscar jogos na IGDB a partir de um termo de busca,
 * usando a linguagem de consulta Apicalypse.
 * <p>
 * Depende do IgdbAuthClient para obter um access_token válido a cada chamada
 * (sem cache de token, mesma decisão consciente do client de autenticação).
 * <p>
 * Documentação: https://api-docs.igdb.com/#game
 */
@Component
public class IgdbGameSearchClient {

    private final WebClient igdbWebClient;
    private final IgdbAuthClient igdbAuthClient;
    private final IgdbProperties igdbProperties;

    public IgdbGameSearchClient(
            @Qualifier("igdbWebClient") WebClient igdbWebClient,
            IgdbAuthClient igdbAuthClient,
            IgdbProperties igdbProperties
    ) {
        this.igdbWebClient = igdbWebClient;
        this.igdbAuthClient = igdbAuthClient;
        this.igdbProperties = igdbProperties;
    }

    /**
     * Busca jogos na IGDB cujo nome corresponda (por relevância) ao termo informado.
     *
     * @param termoBusca o termo digitado pelo admin (ex: "zelda")
     * @return lista de jogos encontrados (até 10 resultados, no momento)
     * @throws IgdbIntegrationException se a IGDB retornar erro ou estiver indisponível
     */
    public List<IgdbGameDto> buscarJogos(String termoBusca) {
        if (termoBusca == null || termoBusca.isBlank()) {
            throw new IllegalArgumentException("Termo de busca não pode ser vazio.");
        }

        String accessToken = igdbAuthClient.obterAccessToken();
        String query = montarQueryBusca(termoBusca);

        try {
            return igdbWebClient.post()
                    .uri("/games")
                    .header("Client-ID", igdbProperties.clientId())
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.TEXT_PLAIN)
                    .bodyValue(query)
                    .retrieve()
                    .bodyToFlux(IgdbGameDto.class)
                    .collectList()
                    .block();

        } catch (WebClientResponseException e) {
            throw new IgdbIntegrationException(
                    "Falha ao buscar jogos na IGDB (status %d): %s"
                            .formatted(e.getStatusCode().value(), e.getMessage()),
                    e
            );
        } catch (Exception e) {
            throw new IgdbIntegrationException(
                    "Erro inesperado ao buscar jogos na IGDB.", e
            );
        }
    }

    private String montarQueryBusca(String termoBusca) {
        String termoSanitizado = termoBusca.replace("\"", "");
        return """
                search "%s";
                fields id,name,summary,first_release_date,rating,cover.url,platforms.name;
                limit 10;
                """.formatted(termoSanitizado);
    }
}