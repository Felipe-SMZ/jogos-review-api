package desafio.review_jogos.service;

import desafio.review_jogos.model.Usuario;
import desafio.review_jogos.model.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        // injeta o @Value manualmente nos testes unitários
        ReflectionTestUtils.setField(tokenService, "secret", "segredo-de-teste-unitario-123");
        usuario = new Usuario(1L, "user@email.com", "hash", Role.ROLE_USER);
    }

    @Test
    @DisplayName("gerarToken: deve gerar token não nulo e não vazio")
    void gerarToken_sucesso() {
        String token = tokenService.gerarToken(usuario);

        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("validarToken: deve retornar o email do usuário")
    void validarToken_retornaEmail() {
        String token = tokenService.gerarToken(usuario);
        String email = tokenService.validarToken(token);

        assertThat(email).isEqualTo("user@email.com");
    }

    @Test
    @DisplayName("validarToken: deve lançar exceção para token inválido")
    void validarToken_tokenInvalido() {
        assertThatThrownBy(() -> tokenService.validarToken("token.invalido.aqui"))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("validarToken: deve lançar exceção para token de outro secret")
    void validarToken_secretDiferente() {
        String token = tokenService.gerarToken(usuario);

        TokenService outroService = new TokenService();
        ReflectionTestUtils.setField(outroService, "secret", "outro-secret-diferente");

        assertThatThrownBy(() -> outroService.validarToken(token))
                .isInstanceOf(Exception.class);
    }
}