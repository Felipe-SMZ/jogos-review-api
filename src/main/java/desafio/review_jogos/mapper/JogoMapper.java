package desafio.review_jogos.mapper;

import desafio.review_jogos.dto.JogoRequestDto;
import desafio.review_jogos.dto.JogoResponseDto;

import desafio.review_jogos.dto.MediaNotasResponseDto;
import desafio.review_jogos.model.Jogo;
import desafio.review_jogos.model.Review;

public class JogoMapper {
    public static JogoResponseDto toResponse(Jogo jogo) {
        return new JogoResponseDto(
                jogo.getId(),
                jogo.getNome(),
                jogo.getGenero(),
                jogo.getPlataforma(),
                jogo.getReviews().stream().map(ReviewMapper::toResponse).toList()
        );
    }

    public static Jogo toEntity(JogoRequestDto dto) {
        return new Jogo(
                dto.id(),
                dto.nome(),
                dto.genero(),
                dto.plataforma()
        );
    }

    public static MediaNotasResponseDto toMediaDto(Jogo jogo) {
        if (jogo == null) return null;

        // Calcula a média usando Stream
        Double media = jogo.getReviews().stream()
                .mapToInt(Review::getNota)
                .average()
                .orElse(0.0);

        return new MediaNotasResponseDto(
                jogo.getId(),
                jogo.getNome(),
                media
        );
    }
}
