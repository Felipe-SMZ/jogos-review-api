package desafio.review_jogos.service;

import desafio.review_jogos.dto.ReviewRequestDto;
import desafio.review_jogos.dto.ReviewResponseDto;
import desafio.review_jogos.exception.RecursoJaExisteException;
import desafio.review_jogos.model.*;
import desafio.review_jogos.model.enums.*;
import desafio.review_jogos.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock ReviewRepository reviewRepository;
    @Mock JogoRepository jogoRepository;

    @InjectMocks ReviewService reviewService;

    private Usuario dono;
    private Usuario outro;
    private Usuario admin;
    private Jogo jogo;
    private Review review;

    @BeforeEach
    void setUp() {

        dono = new Usuario(
                1L,
                "dono@test.com",
                "x",
                "senha",
                Role.ROLE_USER,
                null,
                null
        );

        outro = new Usuario(
                2L,
                "outro@test.com",
                "x",
                "senha",
                Role.ROLE_USER,
                null,
                null
        );

        admin = new Usuario(
                3L,
                "admin@test.com",
                "x",
                "senha",
                Role.ROLE_ADMIN,
                null,
                null
        );

        jogo = new Jogo(
                1L,
                "Game",
                Genero.RPG,
                Plataforma.PC,
                null
        );

        review = new Review(
                1L,
                8,
                "ok",
                jogo,
                dono
        );
    }

    @Test
    void salvar_ok() {
        when(jogoRepository.findById(1L)).thenReturn(Optional.of(jogo));
        when(reviewRepository.existsByJogoIdAndUsuarioId(1L, 1L)).thenReturn(false);
        when(reviewRepository.save(any())).thenReturn(review);

        ReviewResponseDto dto = reviewService.salvar(1L, new ReviewRequestDto(8, "ok"), dono);

        assertThat(dto.nota()).isEqualTo(8);
    }

    @Test
    void salvar_duplicado() {
        when(jogoRepository.findById(1L)).thenReturn(Optional.of(jogo));
        when(reviewRepository.existsByJogoIdAndUsuarioId(1L, 1L)).thenReturn(true);

        assertThatThrownBy(() ->
                reviewService.salvar(1L, new ReviewRequestDto(8, "ok"), dono))
                .isInstanceOf(RecursoJaExisteException.class);
    }

    @Test
    void deletar_owner_ok() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        reviewService.deletar(1L, dono);

        verify(reviewRepository).deleteById(1L);
    }

    @Test
    void deletar_admin_ok() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        reviewService.deletar(1L, admin);

        verify(reviewRepository).deleteById(1L);
    }

    @Test
    void deletar_forbidden() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        assertThatThrownBy(() ->
                reviewService.deletar(1L, outro))
                .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
    }
}