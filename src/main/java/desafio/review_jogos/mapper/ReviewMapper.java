package desafio.review_jogos.mapper;

import desafio.review_jogos.dto.ReviewResponseDto;
import desafio.review_jogos.model.Review;

public class ReviewMapper {
    public static ReviewResponseDto toResponse(Review review) {
        if (review == null) return null;

        return new ReviewResponseDto(
                review.getId(),
                review.getNota(),
                review.getComentario(),
                review.getJogo() != null ? review.getJogo().getId() : null // Pega o ID do jogo
        );
    }
}
