package desafio.review_jogos.service;

import desafio.review_jogos.dto.JogoRequestDto;
import desafio.review_jogos.dto.JogoResponseDto;
import desafio.review_jogos.dto.MediaNotasResponseDto;
import desafio.review_jogos.exception.RecursoJaExisteException;
import desafio.review_jogos.exception.RecursoNaoEncontradoException;
import desafio.review_jogos.model.Jogo;
import desafio.review_jogos.model.enums.Genero;
import desafio.review_jogos.model.enums.Plataforma;
import desafio.review_jogos.repository.JogoRepository;
import desafio.review_jogos.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JogoServiceTest {

    @Mock
    private JogoRepository jogoRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private JogoService jogoService;

    private Jogo jogo;

    @BeforeEach
    void setUp() {
        jogo = new Jogo(1L, "The Witcher 3", Genero.RPG, Plataforma.PC, null);
    }

    @Test
    void salvar_sucesso() {
        when(jogoRepository.existsByNomeIgnoreCase("The Witcher 3")).thenReturn(false);
        when(jogoRepository.save(any())).thenReturn(jogo);

        Jogo result = jogoService.salvar(jogo);

        assertThat(result.getNome()).isEqualTo("The Witcher 3");
    }

    @Test
    void salvar_duplicado() {
        when(jogoRepository.existsByNomeIgnoreCase("The Witcher 3")).thenReturn(true);

        assertThatThrownBy(() -> jogoService.salvar(jogo))
                .isInstanceOf(RecursoJaExisteException.class);

        verify(jogoRepository, never()).save(any());
    }

    @Test
    void buscarPorId_ok() {
        when(jogoRepository.findById(1L)).thenReturn(Optional.of(jogo));

        JogoResponseDto dto = jogoService.buscarPorId(1L);

        assertThat(dto.id()).isEqualTo(1L);
    }

    @Test
    void buscarPorId_notFound() {
        when(jogoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jogoService.buscarPorId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void excluir_ok() {
        when(jogoRepository.findById(1L)).thenReturn(Optional.of(jogo));

        jogoService.excluir(1L);

        verify(jogoRepository).delete(any(Jogo.class));
    }

    @Test
    void atualizar_ok() {
        JogoRequestDto dto = new JogoRequestDto(null, "Elden Ring", Genero.RPG, Plataforma.PS5, null);

        when(jogoRepository.findById(1L)).thenReturn(Optional.of(jogo));
        when(jogoRepository.save(any())).thenReturn(jogo);

        JogoResponseDto result = jogoService.atualizar(1L, dto);

        assertThat(result.nome()).isEqualTo("Elden Ring");
    }

    @Test
    void media_ok() {
        when(jogoRepository.findById(1L)).thenReturn(Optional.of(jogo));
        when(reviewRepository.calcularMediaPorJogoId(1L)).thenReturn(8.0);

        MediaNotasResponseDto dto = jogoService.buscarMediaDoJogo(1L);

        assertThat(dto.media()).isEqualTo(8.0);
    }

    @Test
    void media_null() {
        when(jogoRepository.findById(1L)).thenReturn(Optional.of(jogo));
        when(reviewRepository.calcularMediaPorJogoId(1L)).thenReturn(null);

        MediaNotasResponseDto dto = jogoService.buscarMediaDoJogo(1L);

        assertThat(dto.media()).isEqualTo(0.0);
    }
}