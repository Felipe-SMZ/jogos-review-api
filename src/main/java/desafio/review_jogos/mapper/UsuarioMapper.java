package desafio.review_jogos.mapper;

import desafio.review_jogos.dto.UsuarioResponseDto;
import desafio.review_jogos.model.Usuario;

public class UsuarioMapper {
    public static UsuarioResponseDto toResponse(Usuario usuario) {
        return new UsuarioResponseDto(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNickname(),
                usuario.getRole().name(),
                usuario.getCreatedAt()
        );
    }
}
