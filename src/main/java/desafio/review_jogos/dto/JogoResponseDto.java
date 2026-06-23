package desafio.review_jogos.dto;

import desafio.review_jogos.model.enums.Genero;
import desafio.review_jogos.model.enums.Plataforma;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record JogoResponseDto(
        Long id,
        String nome,
        Genero genero,
        Plataforma plataforma,
        String imageUrl,
        String summary,
        BigDecimal rating,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}