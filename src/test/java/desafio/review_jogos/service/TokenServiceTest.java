package desafio.review_jogos.service;

import desafio.review_jogos.model.Usuario;
import desafio.review_jogos.model.enums.Role;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.junit.jupiter.api.*;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", "segredo-teste");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("user@email.com");
        usuario.setNickname("user");
        usuario.setSenha("hash");
        usuario.setRole(Role.ROLE_USER);
    }

    @Test
    void gerarToken_sucesso() {
        String token = tokenService.gerarToken(usuario);

        assertThat(token).isNotBlank();
    }

    @Test
    void validarToken_sucesso() {
        String token = tokenService.gerarToken(usuario);

        String email = tokenService.validarToken(token);

        assertThat(email).isEqualTo("user@email.com");
    }

    @Test
    void validarToken_invalido() {
        assertThatThrownBy(() ->
                tokenService.validarToken("token.invalido"))
                .isInstanceOf(JWTVerificationException.class);
    }

    @Test
    void validarToken_secretDiferente() {
        String token = tokenService.gerarToken(usuario);

        TokenService outro = new TokenService();
        ReflectionTestUtils.setField(outro, "secret", "outro-secret");

        assertThatThrownBy(() -> outro.validarToken(token))
                .isInstanceOf(JWTVerificationException.class);
    }
}