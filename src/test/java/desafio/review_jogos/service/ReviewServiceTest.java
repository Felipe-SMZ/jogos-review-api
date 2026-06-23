package desafio.review_jogos.service;

import desafio.review_jogos.dto.ReviewRequestDto;
import desafio.review_jogos.dto.ReviewResponseDto;
import desafio.review_jogos.exception.RecursoJaExisteException;
import desafio.review_jogos.model.Jogo;
import desafio.review_jogos.model.Review;
import desafio.review_jogos.model.Usuario;
import desafio.review_jogos.model.enums.Genero;
import desafio.review_jogos.model.enums.Plataforma;
import desafio.review_jogos.model.enums.Role;
import desafio.review_jogos.repository.JogoRepository;
import desafio.review_jogos.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    ReviewRepository reviewRepository;

    @Mock
    JogoRepository jogoRepository;

    @InjectMocks
    ReviewService reviewService;

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
                null,
                "Resumo do jogo",
                new BigDecimal("8.90")
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
                .isInstanceOf(AccessDeniedException.class);
    }
}