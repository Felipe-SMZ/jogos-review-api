package desafio.review_jogos.service;

import desafio.review_jogos.dto.ReviewRequestDto;
import desafio.review_jogos.dto.ReviewResponseDto;
import desafio.review_jogos.exception.RecursoNaoEncontradoException;
import desafio.review_jogos.model.Jogo;
import desafio.review_jogos.model.Review;
import desafio.review_jogos.model.Usuario;
import desafio.review_jogos.model.enums.Genero;
import desafio.review_jogos.model.enums.Plataforma;
import desafio.review_jogos.model.enums.Role;
import desafio.review_jogos.repository.JogoRepository;
import desafio.review_jogos.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private JogoRepository jogoRepository;

    @InjectMocks
    private ReviewService reviewService;

    private Jogo jogo;
    private Usuario dono;
    private Usuario admin;
    private Usuario outro;
    private Review review;

    @BeforeEach
    void setUp() {
        jogo = new Jogo(1L, "The Witcher 3", Genero.RPG, Plataforma.PC);
        dono = new Usuario(1L, "dono@email.com", "hash", Role.ROLE_USER);
        admin = new Usuario(2L, "admin@email.com", "hash", Role.ROLE_ADMIN);
        outro = new Usuario(3L, "outro@email.com", "hash", Role.ROLE_USER);

        review = new Review(1L, 9, "Obra prima", jogo);
        review.setUsuario(dono);
    }

    // ── salvar ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("salvar: deve criar review com sucesso")
    void salvar_sucesso() {
        when(jogoRepository.findById(1L)).thenReturn(Optional.of(jogo));
        when(reviewRepository.save(any())).thenReturn(review);

        ReviewResponseDto resultado = reviewService.salvar(1L, new ReviewRequestDto(9, "Obra prima"), dono);

        assertThat(resultado.nota()).isEqualTo(9);
        assertThat(resultado.jogoId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("salvar: deve lançar exceção quando jogo não existe")
    void salvar_jogoNaoEncontrado() {
        when(jogoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.salvar(99L, new ReviewRequestDto(9, "Ótimo"), dono))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("99");
    }

    // ── deletar ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("deletar: dono pode deletar sua review")
    void deletar_comDono() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        reviewService.deletar(1L, dono);

        verify(reviewRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deletar: admin pode deletar qualquer review")
    void deletar_comAdmin() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        reviewService.deletar(1L, admin);

        verify(reviewRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deletar: outro usuário não pode deletar review alheia")
    void deletar_semPermissao() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        assertThatThrownBy(() -> reviewService.deletar(1L, outro))
                .isInstanceOf(AccessDeniedException.class);

        verify(reviewRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("deletar: deve lançar exceção quando review não existe")
    void deletar_naoEncontrada() {
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.deletar(99L, dono))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    // ── atualizar ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("atualizar: deve atualizar nota e comentário com sucesso")
    void atualizar_sucesso() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any())).thenReturn(review);

        ReviewResponseDto resultado = reviewService.atualizar(1L, new ReviewRequestDto(10, "Perfeito"), dono);

        assertThat(resultado.nota()).isEqualTo(10);
        assertThat(resultado.comentario()).isEqualTo("Perfeito");
    }

    @Test
    @DisplayName("atualizar: deve atualizar só a nota quando comentário for null")
    void atualizar_apenasNota() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any())).thenReturn(review);

        reviewService.atualizar(1L, new ReviewRequestDto(7, null), dono);

        assertThat(review.getNota()).isEqualTo(7);
        assertThat(review.getComentario()).isEqualTo("Obra prima"); // mantido
    }

    @Test
    @DisplayName("atualizar: deve atualizar só o comentário quando nota for null")
    void atualizar_apenasComentario() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any())).thenReturn(review);

        reviewService.atualizar(1L, new ReviewRequestDto(null, "Incrível"), dono);

        assertThat(review.getNota()).isEqualTo(9);        // mantida
        assertThat(review.getComentario()).isEqualTo("Incrível");
    }

    @Test
    @DisplayName("atualizar: outro usuário não pode editar review alheia")
    void atualizar_semPermissao() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        assertThatThrownBy(() -> reviewService.atualizar(1L, new ReviewRequestDto(5, "Ruim"), outro))
                .isInstanceOf(AccessDeniedException.class);

        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("atualizar: deve lançar exceção quando review não existe")
    void atualizar_naoEncontrada() {
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.atualizar(99L, new ReviewRequestDto(8, "Bom"), dono))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }
}