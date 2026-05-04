package desafio.review_jogos.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import desafio.review_jogos.model.Jogo;
import desafio.review_jogos.model.Usuario;
import desafio.review_jogos.model.enums.Genero;
import desafio.review_jogos.model.enums.Plataforma;
import desafio.review_jogos.model.enums.Role;
import desafio.review_jogos.repository.JogoRepository;
import desafio.review_jogos.repository.ReviewRepository;
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
public class ReviewControllerIT {


    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JogoRepository jogoRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String tokenDono;
    private String tokenOutro;
    private String tokenAdmin;
    private Long jogoId;

    @BeforeEach
    void setUp() {
        Usuario dono = usuarioRepository.save(new Usuario(null, "dono@test.com", passwordEncoder.encode("123"), Role.ROLE_USER));
        Usuario outro = usuarioRepository.save(new Usuario(null, "outro@test.com", passwordEncoder.encode("123"), Role.ROLE_USER));
        Usuario admin = usuarioRepository.save(new Usuario(null, "admin@test.com", passwordEncoder.encode("123"), Role.ROLE_ADMIN));

        tokenDono = "Bearer " + tokenService.gerarToken(dono);
        tokenOutro = "Bearer " + tokenService.gerarToken(outro);
        tokenAdmin = "Bearer " + tokenService.gerarToken(admin);

        Jogo jogo = jogoRepository.save(new Jogo(null, "The Witcher 3", Genero.RPG, Plataforma.PC));
        jogoId = jogo.getId();
    }

    // ── POST /jogos/{id}/reviews ──────────────────────────────────────────────

    @Test
    @DisplayName("POST /reviews: cria review com sucesso → 201")
    void criarReview_sucesso() throws Exception {
        var body = Map.of("nota", 9, "comentario", "Obra prima");

        mockMvc.perform(post("/jogos/" + jogoId + "/reviews")
                        .header("Authorization", tokenDono)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nota").value(9));
    }

    @Test
    @DisplayName("POST /reviews: sem autenticação → 403")
    void criarReview_semToken() throws Exception {
        var body = Map.of("nota", 9, "comentario", "Obra prima");

        mockMvc.perform(post("/jogos/" + jogoId + "/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /reviews: jogo inexistente → 404")
    void criarReview_jogoNaoEncontrado() throws Exception {
        var body = Map.of("nota", 9, "comentario", "Ótimo");

        mockMvc.perform(post("/jogos/999/reviews")
                        .header("Authorization", tokenDono)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /reviews: nota inválida → 400")
    void criarReview_notaInvalida() throws Exception {
        var body = Map.of("nota", 15, "comentario", "Nota errada");

        mockMvc.perform(post("/jogos/" + jogoId + "/reviews")
                        .header("Authorization", tokenDono)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    // ── GET /jogos/{id}/reviews ───────────────────────────────────────────────

    @Test
    @DisplayName("GET /reviews: lista reviews sem autenticação → 200")
    void listarReviews_publico() throws Exception {
        mockMvc.perform(get("/jogos/" + jogoId + "/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    // ── DELETE /reviews/{id} ──────────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /reviews/{id}: dono deleta sua review → 204")
    void deletarReview_comDono() throws Exception {
        var body = Map.of("nota", 8, "comentario", "Bom");
        var criado = mockMvc.perform(post("/jogos/" + jogoId + "/reviews")
                        .header("Authorization", tokenDono)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andReturn();

        Long reviewId = objectMapper.readTree(criado.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(delete("/reviews/" + reviewId)
                        .header("Authorization", tokenDono))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /reviews/{id}: outro usuário não pode deletar → 403")
    void deletarReview_semPermissao() throws Exception {
        var body = Map.of("nota", 8, "comentario", "Bom");
        var criado = mockMvc.perform(post("/jogos/" + jogoId + "/reviews")
                        .header("Authorization", tokenDono)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andReturn();

        Long reviewId = objectMapper.readTree(criado.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(delete("/reviews/" + reviewId)
                        .header("Authorization", tokenOutro))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /reviews/{id}: admin pode deletar qualquer review → 204")
    void deletarReview_comAdmin() throws Exception {
        var body = Map.of("nota", 8, "comentario", "Bom");
        var criado = mockMvc.perform(post("/jogos/" + jogoId + "/reviews")
                        .header("Authorization", tokenDono)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andReturn();

        Long reviewId = objectMapper.readTree(criado.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(delete("/reviews/" + reviewId)
                        .header("Authorization", tokenAdmin))
                .andExpect(status().isNoContent());
    }

    // ── PUT /reviews/{id} ─────────────────────────────────────────────────────

    @Test
    @DisplayName("PUT /reviews/{id}: dono atualiza sua review → 200")
    void atualizarReview_comDono() throws Exception {
        var body = Map.of("nota", 7, "comentario", "Bom");
        var criado = mockMvc.perform(post("/jogos/" + jogoId + "/reviews")
                        .header("Authorization", tokenDono)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andReturn();

        Long reviewId = objectMapper.readTree(criado.getResponse().getContentAsString()).get("id").asLong();

        var atualizar = Map.of("nota", 10, "comentario", "Mudei de ideia, é perfeito");
        mockMvc.perform(put("/reviews/" + reviewId)
                        .header("Authorization", tokenDono)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizar)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nota").value(10))
                .andExpect(jsonPath("$.comentario").value("Mudei de ideia, é perfeito"));
    }

    @Test
    @DisplayName("PUT /reviews/{id}: outro usuário não pode editar → 403")
    void atualizarReview_semPermissao() throws Exception {
        var body = Map.of("nota", 7, "comentario", "Bom");
        var criado = mockMvc.perform(post("/jogos/" + jogoId + "/reviews")
                        .header("Authorization", tokenDono)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andReturn();

        Long reviewId = objectMapper.readTree(criado.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(put("/reviews/" + reviewId)
                        .header("Authorization", tokenOutro)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("nota", 1))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /reviews/{id}: review inexistente → 404")
    void atualizarReview_naoEncontrada() throws Exception {
        mockMvc.perform(put("/reviews/999")
                        .header("Authorization", tokenDono)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("nota", 5))))
                .andExpect(status().isNotFound());
    }
}