package desafio.review_jogos.dto;

import desafio.review_jogos.model.enums.Genero;
import desafio.review_jogos.model.enums.Plataforma;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public record JogoResponseDto(
        Long id,
        String nome,
        Genero genero,
        Set<Plataforma> plataformas,
        String imageUrl,
        String summary,
        BigDecimal rating,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}