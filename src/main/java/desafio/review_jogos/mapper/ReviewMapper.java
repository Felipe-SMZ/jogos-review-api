package desafio.review_jogos.mapper;

import desafio.review_jogos.dto.ReviewRequestDto;
import desafio.review_jogos.dto.ReviewResponseDto;
import desafio.review_jogos.model.Review;

public class ReviewMapper {

    public static ReviewResponseDto toResponse(Review review) {
        return new ReviewResponseDto(
                review.getId(),
                review.getNota(),
                review.getComentario(),
                review.getJogo() != null ? review.getJogo().getId() : null,
                review.getUsuario() != null ? review.getUsuario().getNickname() : null,
                review.getCreatedAt()
        );
    }

    public static Review toEntity(ReviewRequestDto dto) {
        Review review = new Review();
        review.setNota(dto.nota());
        review.setComentario(dto.comentario());
        return review;
    }
}