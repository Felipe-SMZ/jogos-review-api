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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JogoServiceTest {

    @Mock
    private JogoRepository jogoRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private JogoService jogoService;

    private Jogo jogo;
    private JogoRequestDto jogoDto;

    @BeforeEach
    void setUp() {
        jogo = new Jogo(
                1L,
                "The Witcher 3",
                Genero.RPG,
                Set.of(Plataforma.PC),
                null,
                "RPG de fantasia com mundo aberto.",
                new BigDecimal("9.50"),
                LocalDateTime.now(),
                LocalDateTime.now(),
                new ArrayList<>()
        );

        jogoDto = new JogoRequestDto(
                null,
                "The Witcher 3",
                Genero.RPG,
                Set.of(Plataforma.PC),
                null,
                "RPG de fantasia com mundo aberto.",
                new BigDecimal("9.50")
        );
    }

    @Test
    void salvar_sucesso() {
        when(jogoRepository.existsByNomeIgnoreCase("The Witcher 3")).thenReturn(false);
        when(jogoRepository.save(any())).thenReturn(jogo);

        Jogo result = jogoService.salvar(jogoDto);

        assertThat(result.getNome()).isEqualTo("The Witcher 3");
        assertThat(result.getSummary()).isEqualTo("RPG de fantasia com mundo aberto.");
        assertThat(result.getRating()).isEqualByComparingTo("9.50");
        assertThat(result.getPlataformas()).containsExactly(Plataforma.PC);
    }

    @Test
    void salvar_duplicado() {
        when(jogoRepository.existsByNomeIgnoreCase("The Witcher 3")).thenReturn(true);

        assertThatThrownBy(() -> jogoService.salvar(jogoDto))
                .isInstanceOf(RecursoJaExisteException.class);

        verify(jogoRepository, never()).save(any());
    }

    @Test
    void buscarPorId_ok() {
        when(jogoRepository.findById(1L)).thenReturn(Optional.of(jogo));

        JogoResponseDto dto = jogoService.buscarPorId(1L);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.nome()).isEqualTo("The Witcher 3");
        assertThat(dto.summary()).isEqualTo("RPG de fantasia com mundo aberto.");
        assertThat(dto.rating()).isEqualByComparingTo("9.50");
        assertThat(dto.plataformas()).containsExactly(Plataforma.PC);
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
        JogoRequestDto dto = new JogoRequestDto(
                null,
                "Elden Ring",
                Genero.RPG,
                Set.of(Plataforma.PLAYSTATION_5, Plataforma.PC),
                null,
                "Action RPG em mundo aberto.",
                new BigDecimal("9.80")
        );

        when(jogoRepository.findById(1L)).thenReturn(Optional.of(jogo));
        when(jogoRepository.existsByNomeIgnoreCase("Elden Ring")).thenReturn(false);
        when(jogoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        JogoResponseDto result = jogoService.atualizar(1L, dto);

        assertThat(result.nome()).isEqualTo("Elden Ring");
        assertThat(result.genero()).isEqualTo(Genero.RPG);
        assertThat(result.plataformas()).containsExactlyInAnyOrder(Plataforma.PLAYSTATION_5, Plataforma.PC);
        assertThat(result.summary()).isEqualTo("Action RPG em mundo aberto.");
        assertThat(result.rating()).isEqualByComparingTo("9.80");
    }

    @Test
    void atualizar_deveLimparSummaryQuandoReceberStringEmBranco() {
        JogoRequestDto dto = new JogoRequestDto(
                null,
                "Elden Ring",
                Genero.RPG,
                Set.of(Plataforma.PLAYSTATION_5),
                null,
                "   ",
                new BigDecimal("9.80")
        );

        when(jogoRepository.findById(1L)).thenReturn(Optional.of(jogo));
        when(jogoRepository.existsByNomeIgnoreCase("Elden Ring")).thenReturn(false);
        when(jogoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        JogoResponseDto result = jogoService.atualizar(1L, dto);

        assertThat(result.summary()).isNull();
        assertThat(result.rating()).isEqualByComparingTo("9.80");
        assertThat(result.plataformas()).containsExactly(Plataforma.PLAYSTATION_5);
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