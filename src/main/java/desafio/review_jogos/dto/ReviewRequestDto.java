package desafio.review_jogos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Range;

public record ReviewRequestDto(
        @NotNull(message = "A nota é obrigatória")
        @Range(min = 1, max = 10, message = "A nota deve estar entre 1 e 10")
        Integer nota,

        @NotBlank(message = "O comentário é obrigatório")
        @Size(max = 500, message = "O comentário deve ter no máximo 500 caracteres")
        String comentario
) {
}