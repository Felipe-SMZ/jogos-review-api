package desafio.review_jogos.dto;

public record ReviewResponseDto(
        Long id,
        Integer nota,
        String comentario,
        Long jogoId

) {
}
