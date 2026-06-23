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
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ReviewControllerIT {

    @Autowired
    private WebApplicationContext context;

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
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        reviewRepository.deleteAll();
        jogoRepository.deleteAll();
        usuarioRepository.deleteAll();

        Usuario dono = usuarioRepository.save(
                new Usuario(null, "dono@test.com", "Dono",
                        passwordEncoder.encode("123456"),
                        Role.ROLE_USER, null, null)
        );

        Usuario outro = usuarioRepository.save(
                new Usuario(null, "outro@test.com", "Outro",
                        passwordEncoder.encode("123456"),
                        Role.ROLE_USER, null, null)
        );

        Usuario admin = usuarioRepository.save(
                new Usuario(null, "admin@test.com", "Adminteste",
                        passwordEncoder.encode("123456"),
                        Role.ROLE_ADMIN, null, null)
        );

        tokenDono = "Bearer " + tokenService.gerarToken(dono);
        tokenOutro = "Bearer " + tokenService.gerarToken(outro);
        tokenAdmin = "Bearer " + tokenService.gerarToken(admin);

        Jogo jogo = jogoRepository.save(
                new Jogo(null, "The Witcher 3", Genero.RPG, Plataforma.PC, null, null, null)
        );

        jogoId = jogo.getId();
    }

    @Test
    @DisplayName("POST /jogos/{id}/reviews: cria review -> 201")
    void criarReview_sucesso() throws Exception {

        var body = Map.of(
                "nota", 9,
                "comentario", "Obra prima"
        );

        mockMvc.perform(post("/jogos/" + jogoId + "/reviews")
                        .header("Authorization", tokenDono)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nota").value(9));
    }

    @Test
    @DisplayName("DELETE /reviews/{id}: outro usuário não pode deletar -> 403")
    void deletarReview_semPermissao() throws Exception {

        var body = Map.of("nota", 8, "comentario", "Bom");

        var result = mockMvc.perform(post("/jogos/" + jogoId + "/reviews")
                        .header("Authorization", tokenDono)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andReturn();

        Long reviewId = objectMapper.readTree(
                result.getResponse().getContentAsString()
        ).get("id").asLong();

        mockMvc.perform(delete("/reviews/" + reviewId)
                        .header("Authorization", tokenOutro))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /reviews/{id}: admin pode deletar -> 204")
    void deletarReview_comAdmin() throws Exception {

        var body = Map.of("nota", 8, "comentario", "Bom");

        var result = mockMvc.perform(post("/jogos/" + jogoId + "/reviews")
                        .header("Authorization", tokenDono)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andReturn();

        Long reviewId = objectMapper.readTree(
                result.getResponse().getContentAsString()
        ).get("id").asLong();

        mockMvc.perform(delete("/reviews/" + reviewId)
                        .header("Authorization", tokenAdmin))
                .andExpect(status().isNoContent());
    }
}