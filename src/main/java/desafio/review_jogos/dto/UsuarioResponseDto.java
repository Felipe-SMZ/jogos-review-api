package desafio.review_jogos.dto;

import java.time.LocalDateTime;

public record UsuarioResponseDto(
        Long id,
        String email,
        String nickname,
        String role,
        LocalDateTime createdAt) {
}
