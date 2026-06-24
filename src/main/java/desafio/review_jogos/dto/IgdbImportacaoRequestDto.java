package desafio.review_jogos.dto;

import desafio.review_jogos.model.enums.Genero;
import jakarta.validation.constraints.NotNull;

public record IgdbImportacaoRequestDto(

        @NotNull(message = "O ID do jogo na IGDB é obrigatório")
        Long igdbId,

        @NotNull(message = "O gênero do jogo é obrigatório")
        Genero genero
) {
}