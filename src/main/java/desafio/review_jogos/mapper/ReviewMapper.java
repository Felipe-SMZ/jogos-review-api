package desafio.review_jogos.mapper;

import desafio.review_jogos.dto.ReviewRequestDto;
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

    public static Review toEntity(ReviewRequestDto dto) {
        if (dto == null) return null;

        Review review = new Review();
        review.setId(dto.id());
        review.setNota(dto.nota());
        review.setComentario(dto.comentario());
        // O Jogo é setado no Service, pois ele vem do banco
        return review;
    }
}
