package desafio.review_jogos.service;

import desafio.review_jogos.dto.UsuarioRequestDto;
import desafio.review_jogos.exception.RecursoJaExisteException;
import desafio.review_jogos.model.Usuario;
import desafio.review_jogos.model.enums.Role;
import desafio.review_jogos.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void cadastrarUsuario(UsuarioRequestDto dto) {
        if (usuarioRepository.findByEmail(dto.email()).isPresent()) {
            throw new RecursoJaExisteException("E-mail já cadastrado");
        }

        if (usuarioRepository.findByNickname(dto.nickname()).isPresent()) {
            throw new RecursoJaExisteException("Nickname já cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(dto.email());
        usuario.setNickname(dto.nickname());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setRole(Role.ROLE_USER);

        usuarioRepository.save(usuario);
    }

}
