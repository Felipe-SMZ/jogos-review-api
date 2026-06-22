package desafio.review_jogos.dto;

import desafio.review_jogos.model.enums.Genero;
import desafio.review_jogos.model.enums.Plataforma;
import jakarta.validation.constraints.NotNull;

public record IgdbImportacaoRequestDto(

        @NotNull(message = "O ID do jogo na IGDB é obrigatório")
        Long igdbId,

        @NotNull(message = "O gênero do jogo é obrigatório")
        Genero genero,

        @NotNull(message = "A plataforma do jogo é obrigatória")
        Plataforma plataforma
) {
}