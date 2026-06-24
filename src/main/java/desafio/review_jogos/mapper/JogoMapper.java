package desafio.review_jogos.mapper;

import desafio.review_jogos.dto.JogoRequestDto;
import desafio.review_jogos.dto.JogoResponseDto;
import desafio.review_jogos.model.Jogo;

public class JogoMapper {

    public static JogoResponseDto toResponse(Jogo jogo) {
        return new JogoResponseDto(
                jogo.getId(),
                jogo.getNome(),
                jogo.getGenero(),
                jogo.getPlataformas(),
                jogo.getImageUrl(),
                jogo.getSummary(),
                jogo.getRating(),
                jogo.getCreatedAt(),
                jogo.getUpdatedAt()
        );
    }

    public static Jogo toEntity(JogoRequestDto dto) {
        Jogo jogo = new Jogo();
        jogo.setNome(dto.nome());
        jogo.setGenero(dto.genero());
        jogo.setPlataformas(dto.plataformas());
        jogo.setImageUrl(dto.imageUrl());
        jogo.setSummary(dto.summary());
        jogo.setRating(dto.rating());
        return jogo;
    }
}