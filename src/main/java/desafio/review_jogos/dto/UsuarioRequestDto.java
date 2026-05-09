package desafio.review_jogos.dto;

import desafio.review_jogos.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioRequestDto(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 3, max = 30) String nickname,
        @NotBlank @Size(min = 8) String senha
) {
}
