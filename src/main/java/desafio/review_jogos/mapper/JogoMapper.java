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
                jogo.getPlataforma(),
                jogo.getImageUrl(),
                jogo.getCreatedAt(),
                jogo.getUpdatedAt()
        );
    }

    public static Jogo toEntity(JogoRequestDto dto) {
        Jogo jogo = new Jogo();
        jogo.setNome(dto.nome());
        jogo.setGenero(dto.genero());
        jogo.setPlataforma(dto.plataforma());
        jogo.setImageUrl(dto.imageUrl());
        return jogo;
    }
}
