package desafio.review_jogos.dto;

import desafio.review_jogos.validation.OnCreate;
import desafio.review_jogos.validation.OnUpdate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Range;

public record ReviewRequestDto(
        @Null(groups = OnCreate.class, message = "O id deve ser nulo ao criar uma nova review.")
        @NotNull(groups = OnUpdate.class, message = "O id é obrigatório para atualização ou pesquisa.") Long id,

        @NotNull(message = "A nota é obrigatória")
        @Range(min = 1, max = 10, message = "A nota deve estar entre 1 e 10")
        Long nota,

        @NotBlank(message = "O comentário é obrigatório")
        @Size(max = 500, message = "O comentário deve ter no máximo 500 caracteres")
        String comentario
        ) {
}
