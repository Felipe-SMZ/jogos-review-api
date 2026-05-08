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
class JogoControllerIT {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private JogoRepository jogoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String tokenAdmin;
    private String tokenUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        jogoRepository.deleteAll();
        usuarioRepository.deleteAll();

        Usuario admin = usuarioRepository.save(
                new Usuario(null, "admin@test.com", "Admin",
                        passwordEncoder.encode("123456"),
                        Role.ROLE_ADMIN, null, null)
        );

        Usuario user = usuarioRepository.save(
                new Usuario(null, "user@test.com", "User",
                        passwordEncoder.encode("123456"),
                        Role.ROLE_USER, null, null)
        );

        tokenAdmin = "Bearer " + tokenService.gerarToken(admin);
        tokenUser = "Bearer " + tokenService.gerarToken(user);
    }

    @Test
    @DisplayName("POST /jogos: admin cria jogo -> 201")
    void criarJogo_comAdmin() throws Exception {

        var body = Map.of(
                "nome", "Elden Ring",
                "genero", "RPG",
                "plataforma", "PC"
        );

        mockMvc.perform(post("/jogos")
                        .header("Authorization", tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Elden Ring"));
    }

    @Test
    @DisplayName("POST /jogos: user não pode criar -> 403")
    void criarJogo_comUser() throws Exception {

        var body = Map.of(
                "nome", "Elden Ring",
                "genero", "RPG",
                "plataforma", "PC"
        );

        mockMvc.perform(post("/jogos")
                        .header("Authorization", tokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden());
    }
}