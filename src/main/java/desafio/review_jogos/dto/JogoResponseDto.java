package desafio.review_jogos.dto;

import desafio.review_jogos.model.enums.Genero;
import desafio.review_jogos.model.enums.Plataforma;

import java.util.List;

public record JogoResponseDto(
        Long id,
        String nome,
        Genero genero,
        Plataforma plataforma,
        List<ReviewResponseDto> reviews
) {
}
