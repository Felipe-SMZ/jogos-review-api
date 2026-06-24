package desafio.review_jogos.integration;

import desafio.review_jogos.model.Usuario;
import desafio.review_jogos.model.enums.Role;
import desafio.review_jogos.repository.UsuarioRepository;
import desafio.review_jogos.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AutenticacaoControllerIT {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private String tokenUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        usuarioRepository.deleteAll();

        Usuario user = usuarioRepository.save(
                new Usuario(null, "user@test.com", "testeuser",
                        passwordEncoder.encode("senha123"),
                        Role.ROLE_USER, null, null)
        );

        tokenUser = "Bearer " + tokenService.gerarToken(user);
    }

    @Test
    @DisplayName("POST /auth/registrar: cria usuário -> 201")
    void registrar_sucesso() throws Exception {
        mockMvc.perform(post("/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "novo@teste.com",
                                    "nickname": "novousuario",
                                    "senha": "senha123"
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /auth/registrar: email duplicado -> 409")
    void registrar_emailDuplicado() throws Exception {
        mockMvc.perform(post("/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "user@test.com",
                                    "nickname": "outronick",
                                    "senha": "senha123"
                                }
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /auth/registrar: nickname duplicado -> 409")
    void registrar_nicknameDuplicado() throws Exception {
        mockMvc.perform(post("/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "outro@teste.com",
                                    "nickname": "testeuser",
                                    "senha": "senha123"
                                }
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /auth/login: credenciais válidas -> 200 com token")
    void login_sucesso() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "user@test.com",
                                    "senha": "senha123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @DisplayName("POST /auth/login: senha errada -> 401")
    void login_senhaErrada() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "user@test.com",
                                    "senha": "senhaerrada"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /auth/me: token válido -> retorna dados do usuário")
    void me_retornaDadosDoUsuarioAutenticado() throws Exception {
        mockMvc.perform(get("/auth/me")
                        .header("Authorization", tokenUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@test.com"))
                .andExpect(jsonPath("$.nickname").value("testeuser"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    @DisplayName("GET /auth/me: sem token -> 401")
    void me_semToken_retorna401() throws Exception {
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /auth/me: token inválido -> 401")
    void me_tokenInvalido_retorna401() throws Exception {
        mockMvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer token.invalido.aqui"))
                .andExpect(status().isUnauthorized());
    }
}