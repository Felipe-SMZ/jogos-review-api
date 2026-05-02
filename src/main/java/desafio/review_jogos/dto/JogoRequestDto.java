package desafio.review_jogos.dto;

import desafio.review_jogos.model.enums.Genero;
import desafio.review_jogos.model.enums.Plataforma;
import desafio.review_jogos.validation.OnCreate;
import desafio.review_jogos.validation.OnUpdate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

public record JogoRequestDto(
        @Null(groups = OnCreate.class, message = "O ID do jogo deve ser nulo ao criar um novo jogo")
        @NotNull(groups = OnUpdate.class, message = "O ID do jogo é obrigatório para atualização ou pesquisa") Long id,

        @NotBlank(message = "O nome do jogo é obrigatório")
        @Size(max = 100, message = "O nome do jogo deve ter no máximo 100 caracteres")
        String nome,

        @NotNull(message = "O gênero do jogo é obrigatório")
        Genero genero,

        @NotNull(message = "A plataforma do jogo é obrigatória")
        Plataforma plataforma
) {
}
