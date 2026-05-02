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
                review.getJogo() != null ? review.getJogo().getId() : null
        );
    }

    public static Review toEntity(ReviewRequestDto dto) {
        if (dto == null) return null;

        Review review = new Review();
        review.setNota(dto.nota());       // ✅ removido dto.id() e dto.jogoId()
        review.setComentario(dto.comentario());
        return review;
    }
}