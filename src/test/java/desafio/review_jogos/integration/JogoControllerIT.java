package desafio.review_jogos.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import desafio.review_jogos.model.Usuario;
import desafio.review_jogos.model.enums.Role;
import desafio.review_jogos.repository.JogoRepository;
import desafio.review_jogos.repository.UsuarioRepository;
import desafio.review_jogos.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class JogoControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JogoRepository jogoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String tokenAdmin;
    private String tokenUser;

    @BeforeEach
    void setUp() {
        Usuario admin = usuarioRepository.save(
                new Usuario(null, "admin@test.com", passwordEncoder.encode("123456"), Role.ROLE_ADMIN));
        Usuario user = usuarioRepository.save(
                new Usuario(null, "user@test.com", passwordEncoder.encode("123456"), Role.ROLE_USER));

        tokenAdmin = "Bearer " + tokenService.gerarToken(admin);
        tokenUser = "Bearer " + tokenService.gerarToken(user);
    }

    // ── POST /jogos ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /jogos: admin cria jogo com sucesso → 201")
    void criarJogo_comAdmin() throws Exception {
        var body = Map.of("nome", "Elden Ring", "genero", "RPG", "plataforma", "PS5");

        mockMvc.perform(post("/jogos")
                        .header("Authorization", tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Elden Ring"));
    }

    @Test
    @DisplayName("POST /jogos: user comum não pode criar jogo → 403")
    void criarJogo_comUser() throws Exception {
        var body = Map.of("nome", "Elden Ring", "genero", "RPG", "plataforma", "PS5");

        mockMvc.perform(post("/jogos")
                        .header("Authorization", tokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /jogos: sem token → 403")
    void criarJogo_semToken() throws Exception {
        var body = Map.of("nome", "Elden Ring", "genero", "RPG", "plataforma", "PS5");

        mockMvc.perform(post("/jogos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /jogos: jogo duplicado → 409")
    void criarJogo_duplicado() throws Exception {
        var body = Map.of("nome", "Elden Ring", "genero", "RPG", "plataforma", "PS5");

        mockMvc.perform(post("/jogos")
                        .header("Authorization", tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/jogos")
                        .header("Authorization", tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict());
    }

    // ── GET /jogos ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /jogos: lista jogos sem autenticação → 200")
    void listarJogos_publico() throws Exception {
        mockMvc.perform(get("/jogos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    // ── GET /jogos/{id} ───────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /jogos/{id}: jogo inexistente → 404")
    void buscarJogo_naoEncontrado() throws Exception {
        mockMvc.perform(get("/jogos/999"))
                .andExpect(status().isNotFound());
    }

    // ── PUT /jogos/{id} ───────────────────────────────────────────────────────

    @Test
    @DisplayName("PUT /jogos/{id}: admin atualiza jogo → 200")
    void atualizarJogo_comAdmin() throws Exception {
        var criar = Map.of("nome", "Jogo Antigo", "genero", "RPG", "plataforma", "PC");
        var criado = mockMvc.perform(post("/jogos")
                        .header("Authorization", tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criar)))
                .andReturn();

        Long id = objectMapper.readTree(criado.getResponse().getContentAsString()).get("id").asLong();

        var atualizar = Map.of("nome", "Jogo Novo", "genero", "ACAO", "plataforma", "PS5");
        mockMvc.perform(put("/jogos/" + id)
                        .header("Authorization", tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizar)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Jogo Novo"));
    }

    // ── DELETE /jogos/{id} ────────────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /jogos/{id}: admin deleta jogo → 204")
    void deletarJogo_comAdmin() throws Exception {
        var criar = Map.of("nome", "Jogo Para Deletar", "genero", "RPG", "plataforma", "PC");
        var criado = mockMvc.perform(post("/jogos")
                        .header("Authorization", tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criar)))
                .andReturn();

        Long id = objectMapper.readTree(criado.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(delete("/jogos/" + id)
                        .header("Authorization", tokenAdmin))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /jogos/{id}: user não pode deletar → 403")
    void deletarJogo_comUser() throws Exception {
        var criar = Map.of("nome", "Jogo Protegido", "genero", "RPG", "plataforma", "PC");
        var criado = mockMvc.perform(post("/jogos")
                        .header("Authorization", tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criar)))
                .andReturn();

        Long id = objectMapper.readTree(criado.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(delete("/jogos/" + id)
                        .header("Authorization", tokenUser))
                .andExpect(status().isForbidden());
    }
}