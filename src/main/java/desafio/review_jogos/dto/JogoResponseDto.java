package desafio.review_jogos.dto;

import java.util.List;

public record JogoResponseDto(
        Long id,
        String nome,
        String genero,
        String plataforma,
        List<ReviewResponseDto> reviews
) {
}
