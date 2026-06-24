package desafio.review_jogos.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import desafio.review_jogos.dto.IgdbImportacaoRequestDto;
import desafio.review_jogos.dto.JogoResponseDto;
import desafio.review_jogos.exception.IgdbIntegrationException;
import desafio.review_jogos.exception.RecursoJaExisteException;
import desafio.review_jogos.exception.RecursoNaoEncontradoException;
import desafio.review_jogos.model.Usuario;
import desafio.review_jogos.model.enums.Genero;
import desafio.review_jogos.model.enums.Plataforma;
import desafio.review_jogos.model.enums.Role;
import desafio.review_jogos.repository.UsuarioRepository;
import desafio.review_jogos.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import desafio.review_jogos.service.IgdbImportacaoService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AdminJogoControllerIT {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private IgdbImportacaoService igdbImportacaoService;

    private String tokenAdmin;
    private String tokenUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        usuarioRepository.deleteAll();

        Usuario admin = usuarioRepository.save(
                new Usuario(
                        null,
                        "admin.it@test.com",
                        "Admin IT",
                        passwordEncoder.encode("123456"),
                        Role.ROLE_ADMIN,
                        null,
                        null
                )
        );

        Usuario user = usuarioRepository.save(
                new Usuario(
                        null,
                        "user.it@test.com",
                        "User IT",
                        passwordEncoder.encode("123456"),
                        Role.ROLE_USER,
                        null,
                        null
                )
        );

        tokenAdmin = "Bearer " + tokenService.gerarToken(admin);
        tokenUser = "Bearer " + tokenService.gerarToken(user);

        Mockito.reset(igdbImportacaoService);
    }

    @Test
    @DisplayName("POST /admin/jogos/importar: admin importa jogo -> 201")
    void importarJogo_comAdmin() throws Exception {
        var body = new IgdbImportacaoRequestDto(10L, Genero.RPG);

        var response = new JogoResponseDto(
                1L,
                "Elden Ring",
                Genero.RPG,
                Set.of(Plataforma.PC, Plataforma.PLAYSTATION_5),
                "https://images.test/elden-ring.jpg",
                "Resumo teste",
                new BigDecimal("9.50"),
                LocalDateTime.of(2026, 6, 23, 20, 0),
                LocalDateTime.of(2026, 6, 23, 20, 5)
        );

        Mockito.when(igdbImportacaoService.importarJogo(any(IgdbImportacaoRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/admin/jogos/importar")
                        .with(csrf())
                        .header("Authorization", tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Elden Ring"))
                .andExpect(jsonPath("$.genero").value("RPG"))
                .andExpect(jsonPath("$.plataformas").isArray())
                .andExpect(jsonPath("$.imageUrl").value("https://images.test/elden-ring.jpg"))
                .andExpect(jsonPath("$.summary").value("Resumo teste"))
                .andExpect(jsonPath("$.rating").value(9.5))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    @DisplayName("POST /admin/jogos/importar: user nao pode importar -> 403")
    void importarJogo_comUser() throws Exception {
        var body = new IgdbImportacaoRequestDto(10L, Genero.RPG);

        mockMvc.perform(post("/admin/jogos/importar")
                        .with(csrf())
                        .header("Authorization", tokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /admin/jogos/importar: sem token -> 4xx")
    void importarJogo_semToken() throws Exception {
        var body = new IgdbImportacaoRequestDto(10L, Genero.RPG);

        mockMvc.perform(post("/admin/jogos/importar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("POST /admin/jogos/importar: payload invalido -> 400")
    void importarJogo_payloadInvalido() throws Exception {
        var body = """
                {
                  "genero": "RPG"
                }
                """;

        mockMvc.perform(post("/admin/jogos/importar")
                        .with(csrf())
                        .header("Authorization", tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /admin/jogos/importar: jogo nao encontrado -> 404")
    void importarJogo_quandoNaoEncontrado() throws Exception {
        var body = new IgdbImportacaoRequestDto(999L, Genero.RPG);

        Mockito.doThrow(new RecursoNaoEncontradoException("Jogo não encontrado na IGDB."))
                .when(igdbImportacaoService)
                .importarJogo(any(IgdbImportacaoRequestDto.class));

        mockMvc.perform(post("/admin/jogos/importar")
                        .with(csrf())
                        .header("Authorization", tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Jogo não encontrado na IGDB."));
    }

    @Test
    @DisplayName("POST /admin/jogos/importar: jogo ja existe -> 409")
    void importarJogo_quandoJaExiste() throws Exception {
        var body = new IgdbImportacaoRequestDto(10L, Genero.RPG);

        Mockito.doThrow(new RecursoJaExisteException("Jogo já existe no sistema."))
                .when(igdbImportacaoService)
                .importarJogo(any(IgdbImportacaoRequestDto.class));

        mockMvc.perform(post("/admin/jogos/importar")
                        .with(csrf())
                        .header("Authorization", tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.erro").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Jogo já existe no sistema."));
    }

    @Test
    @DisplayName("POST /admin/jogos/importar: erro na integracao com IGDB -> 502")
    void importarJogo_quandoIgdbFalha() throws Exception {
        var body = new IgdbImportacaoRequestDto(10L, Genero.RPG);

        Mockito.doThrow(new IgdbIntegrationException("Falha na comunicação com IGDB"))
                .when(igdbImportacaoService)
                .importarJogo(any(IgdbImportacaoRequestDto.class));

        mockMvc.perform(post("/admin/jogos/importar")
                        .with(csrf())
                        .header("Authorization", tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andDo(print())
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(502))
                .andExpect(jsonPath("$.erro").value("Bad Gateway"))
                .andExpect(jsonPath("$.message").value("Serviço de busca de jogos temporariamente indisponível, tente novamente mais tarde"));
    }

    @Test
    @DisplayName("POST /admin/jogos/importar: argumento invalido -> 400")
    void importarJogo_quandoArgumentoInvalido() throws Exception {
        var body = new IgdbImportacaoRequestDto(10L, Genero.RPG);

        Mockito.doThrow(new IllegalArgumentException("Dados inválidos para importação"))
                .when(igdbImportacaoService)
                .importarJogo(any(IgdbImportacaoRequestDto.class));

        mockMvc.perform(post("/admin/jogos/importar")
                        .with(csrf())
                        .header("Authorization", tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Dados inválidos para importação"));
    }

    @Test
    @DisplayName("POST /admin/jogos/importar: erro inesperado -> 500")
    void importarJogo_quandoErroInterno() throws Exception {
        var body = new IgdbImportacaoRequestDto(10L, Genero.RPG);

        Mockito.doThrow(new RuntimeException("Erro inesperado"))
                .when(igdbImportacaoService)
                .importarJogo(any(IgdbImportacaoRequestDto.class));

        mockMvc.perform(post("/admin/jogos/importar")
                        .with(csrf())
                        .header("Authorization", tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.erro").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("Erro interno no servidor."));
    }
}