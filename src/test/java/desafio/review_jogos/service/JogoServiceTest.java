package desafio.review_jogos.service;

import desafio.review_jogos.dto.JogoRequestDto;
import desafio.review_jogos.dto.JogoResponseDto;
import desafio.review_jogos.dto.MediaNotasResponseDto;
import desafio.review_jogos.exception.RecursoJaExisteException;
import desafio.review_jogos.exception.RecursoNaoEncontradoException;
import desafio.review_jogos.model.Jogo;
import desafio.review_jogos.model.Review;
import desafio.review_jogos.model.enums.Genero;
import desafio.review_jogos.model.enums.Plataforma;
import desafio.review_jogos.repository.JogoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JogoServiceTest {

    @Mock
    private JogoRepository jogoRepository;

    @InjectMocks
    private JogoService jogoService;

    private Jogo jogo;

    @BeforeEach
    void setUp() {
        jogo = new Jogo(1L, "The Witcher 3", Genero.RPG, Plataforma.PC);
    }

    // ── salvar ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("salvar: deve salvar jogo com sucesso")
    void salvar_sucesso() {
        when(jogoRepository.existsByNomeIgnoreCase("The Witcher 3")).thenReturn(false);
        when(jogoRepository.save(any())).thenReturn(jogo);

        Jogo resultado = jogoService.salvar(jogo);

        assertThat(resultado.getNome()).isEqualTo("The Witcher 3");
        verify(jogoRepository).save(jogo);
    }

    @Test
    @DisplayName("salvar: deve lançar exceção quando jogo já existe")
    void salvar_jogoJaExiste() {
        when(jogoRepository.existsByNomeIgnoreCase("The Witcher 3")).thenReturn(true);

        assertThatThrownBy(() -> jogoService.salvar(jogo))
                .isInstanceOf(RecursoJaExisteException.class)
                .hasMessageContaining("The Witcher 3");

        verify(jogoRepository, never()).save(any());
    }

    @Test
    @DisplayName("salvar: deve fazer trim no nome antes de salvar")
    void salvar_trimNome() {
        jogo.setNome("  The Witcher 3  ");
        when(jogoRepository.existsByNomeIgnoreCase("The Witcher 3")).thenReturn(false);
        when(jogoRepository.save(any())).thenReturn(jogo);

        jogoService.salvar(jogo);

        assertThat(jogo.getNome()).isEqualTo("The Witcher 3");
    }

    // ── buscarPorId ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("buscarPorId: deve retornar jogo quando encontrado")
    void buscarPorId_sucesso() {
        when(jogoRepository.findById(1L)).thenReturn(Optional.of(jogo));

        JogoResponseDto resultado = jogoService.buscarPorId(1L);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nome()).isEqualTo("The Witcher 3");
    }

    @Test
    @DisplayName("buscarPorId: deve lançar exceção quando não encontrado")
    void buscarPorId_naoEncontrado() {
        when(jogoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jogoService.buscarPorId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("99");
    }

    // ── excluir ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("excluir: deve deletar jogo com sucesso")
    void excluir_sucesso() {
        when(jogoRepository.findById(1L)).thenReturn(Optional.of(jogo));

        jogoService.excluir(1L);

        verify(jogoRepository).delete(jogo);
    }

    @Test
    @DisplayName("excluir: deve lançar exceção quando jogo não existe")
    void excluir_naoEncontrado() {
        when(jogoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jogoService.excluir(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verify(jogoRepository, never()).delete(any(Jogo.class));
    }

    // ── atualizar ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("atualizar: deve atualizar jogo com sucesso")
    void atualizar_sucesso() {
        JogoRequestDto dto = new JogoRequestDto(null, "Elden Ring", Genero.RPG, Plataforma.PS5);
        Jogo atualizado = new Jogo(1L, "Elden Ring", Genero.RPG, Plataforma.PS5);

        when(jogoRepository.findById(1L)).thenReturn(Optional.of(jogo));
        when(jogoRepository.save(any())).thenReturn(atualizado);

        JogoResponseDto resultado = jogoService.atualizar(1L, dto);

        assertThat(resultado.nome()).isEqualTo("Elden Ring");
        assertThat(resultado.plataforma()).isEqualTo(Plataforma.PS5);
    }

    @Test
    @DisplayName("atualizar: deve lançar exceção quando jogo não existe")
    void atualizar_naoEncontrado() {
        JogoRequestDto dto = new JogoRequestDto(null, "Elden Ring", Genero.RPG, Plataforma.PS5);

        when(jogoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jogoService.atualizar(99L, dto))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    // ── buscarMediaDoJogo ─────────────────────────────────────────────────────

    @Test
    @DisplayName("buscarMediaDoJogo: deve calcular média corretamente")
    void buscarMediaDoJogo_sucesso() {
        Review r1 = new Review(1L, 8, "Ótimo", jogo);
        Review r2 = new Review(2L, 6, "Razoável", jogo);
        jogo.setReviews(List.of(r1, r2));

        when(jogoRepository.findById(1L)).thenReturn(Optional.of(jogo));

        MediaNotasResponseDto resultado = jogoService.buscarMediaDoJogo(1L);

        assertThat(resultado.notas()).isEqualTo(7.0);
    }

    @Test
    @DisplayName("buscarMediaDoJogo: deve retornar 0.0 quando jogo não tem reviews")
    void buscarMediaDoJogo_semReviews() {
        when(jogoRepository.findById(1L)).thenReturn(Optional.of(jogo));

        MediaNotasResponseDto resultado = jogoService.buscarMediaDoJogo(1L);

        assertThat(resultado.notas()).isEqualTo(0.0);
    }
}