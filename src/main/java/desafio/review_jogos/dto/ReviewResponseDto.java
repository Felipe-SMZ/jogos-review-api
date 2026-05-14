package desafio.review_jogos.dto;

import java.time.LocalDateTime;

public record ReviewResponseDto(
        Long id,
        Integer nota,
        String comentario,
        Long jogoId,
        Long usuarioId,
        String nickname,
        LocalDateTime createdAt

) {
}
