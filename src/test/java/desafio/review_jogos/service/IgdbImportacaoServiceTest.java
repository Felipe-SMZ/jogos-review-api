package desafio.review_jogos.service;

import desafio.review_jogos.client.IgdbGameSearchClient;
import desafio.review_jogos.client.dto.IgdbGameDto;
import desafio.review_jogos.dto.IgdbImportacaoRequestDto;
import desafio.review_jogos.dto.JogoRequestDto;
import desafio.review_jogos.dto.JogoResponseDto;
import desafio.review_jogos.exception.RecursoNaoEncontradoException;
import desafio.review_jogos.model.Jogo;
import desafio.review_jogos.model.enums.Genero;
import desafio.review_jogos.model.enums.Plataforma;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IgdbImportacaoServiceTest {

    @Mock
    private IgdbGameSearchClient igdbGameSearchClient;

    @Mock
    private JogoService jogoService;

    @InjectMocks
    private IgdbImportacaoService igdbImportacaoService;

    @Test
    @DisplayName("importarJogo deve mapear resposta da IGDB e delegar salvamento")
    void importarJogo_deveImportarComSucesso() {
        var request = new IgdbImportacaoRequestDto(10L, Genero.RPG);

        var igdbGame = new IgdbGameDto(
                10L,
                "Elden Ring",
                "Resumo do jogo",
                1672444800L,
                96.0,
                new IgdbGameDto.IgdbCoverDto("https://images.igdb.com/cover.jpg"),
                List.of(
                        new IgdbGameDto.IgdbPlatformDto("PC"),
                        new IgdbGameDto.IgdbPlatformDto("PlayStation 5")
                )
        );

        var jogoSalvo = new Jogo();
        jogoSalvo.setId(1L);
        jogoSalvo.setNome("Elden Ring");
        jogoSalvo.setGenero(Genero.RPG);
        jogoSalvo.setPlataformas(Set.of(Plataforma.PC, Plataforma.PLAYSTATION_5));
        jogoSalvo.setImageUrl("https://images.igdb.com/cover.jpg");
        jogoSalvo.setSummary("Resumo do jogo");
        jogoSalvo.setRating(BigDecimal.valueOf(96.0));
        jogoSalvo.setCreatedAt(LocalDateTime.of(2026, 6, 23, 21, 0));
        jogoSalvo.setUpdatedAt(LocalDateTime.of(2026, 6, 23, 21, 1));

        when(igdbGameSearchClient.buscarPorId(10L)).thenReturn(igdbGame);
        when(jogoService.salvar(any(JogoRequestDto.class))).thenReturn(jogoSalvo);

        JogoResponseDto response = igdbImportacaoService.importarJogo(request);

        ArgumentCaptor<JogoRequestDto> captor = ArgumentCaptor.forClass(JogoRequestDto.class);
        verify(jogoService).salvar(captor.capture());

        JogoRequestDto enviado = captor.getValue();

        assertThat(enviado.nome()).isEqualTo("Elden Ring");
        assertThat(enviado.genero()).isEqualTo(Genero.RPG);
        assertThat(enviado.imageUrl()).isEqualTo("https://images.igdb.com/cover.jpg");
        assertThat(enviado.summary()).isEqualTo("Resumo do jogo");
        assertThat(enviado.plataformas())
                .containsExactlyInAnyOrder(Plataforma.PC, Plataforma.PLAYSTATION_5);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.nome()).isEqualTo("Elden Ring");
        assertThat(response.genero()).isEqualTo(Genero.RPG);
        assertThat(response.imageUrl()).isEqualTo("https://images.igdb.com/cover.jpg");
        assertThat(response.summary()).isEqualTo("Resumo do jogo");
        assertThat(response.plataformas())
                .containsExactlyInAnyOrder(Plataforma.PC, Plataforma.PLAYSTATION_5);
    }

    @Test
    @DisplayName("importarJogo deve lançar exceção quando jogo nao for encontrado")
    void importarJogo_quandoNaoEncontrarJogo() {
        var request = new IgdbImportacaoRequestDto(999L, Genero.RPG);

        when(igdbGameSearchClient.buscarPorId(999L))
                .thenThrow(new RecursoNaoEncontradoException("Jogo não encontrado na IGDB."));

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> igdbImportacaoService.importarJogo(request)
        );
    }

    @Test
    @DisplayName("importarJogo deve enviar imageUrl nula quando jogo vier sem capa")
    void importarJogo_deveEnviarImageUrlNula_quandoJogoVierSemCapa() {
        var request = new IgdbImportacaoRequestDto(10L, Genero.RPG);

        var igdbGame = new IgdbGameDto(
                10L,
                "Elden Ring",
                "Resumo do jogo",
                1672444800L,
                96.0,
                null,
                List.of(
                        new IgdbGameDto.IgdbPlatformDto("PC"),
                        new IgdbGameDto.IgdbPlatformDto("PlayStation 5")
                )
        );

        var jogoSalvo = new Jogo();
        jogoSalvo.setId(1L);
        jogoSalvo.setNome("Elden Ring");
        jogoSalvo.setGenero(Genero.RPG);
        jogoSalvo.setPlataformas(Set.of(Plataforma.PC, Plataforma.PLAYSTATION_5));
        jogoSalvo.setImageUrl(null);
        jogoSalvo.setSummary("Resumo do jogo");
        jogoSalvo.setRating(BigDecimal.valueOf(96.0));
        jogoSalvo.setCreatedAt(LocalDateTime.of(2026, 6, 23, 21, 0));
        jogoSalvo.setUpdatedAt(LocalDateTime.of(2026, 6, 23, 21, 1));

        when(igdbGameSearchClient.buscarPorId(10L)).thenReturn(igdbGame);
        when(jogoService.salvar(any(JogoRequestDto.class))).thenReturn(jogoSalvo);

        igdbImportacaoService.importarJogo(request);

        ArgumentCaptor<JogoRequestDto> captor = ArgumentCaptor.forClass(JogoRequestDto.class);
        verify(jogoService).salvar(captor.capture());

        JogoRequestDto enviado = captor.getValue();

        assertThat(enviado.imageUrl()).isNull();
        assertThat(enviado.summary()).isEqualTo("Resumo do jogo");
        assertThat(enviado.rating()).isEqualByComparingTo("96.00");
    }

    @Test
    @DisplayName("importarJogo deve enviar rating nulo quando jogo vier sem rating")
    void importarJogo_deveEnviarRatingNulo_quandoJogoVierSemRating() {
        var request = new IgdbImportacaoRequestDto(10L, Genero.RPG);

        var igdbGame = new IgdbGameDto(
                10L,
                "Elden Ring",
                "Resumo do jogo",
                1672444800L,
                null,
                new IgdbGameDto.IgdbCoverDto("//images.igdb.com/igdb/image/upload/t_thumb/cover.jpg"),
                List.of(
                        new IgdbGameDto.IgdbPlatformDto("PC"),
                        new IgdbGameDto.IgdbPlatformDto("PlayStation 5")
                )
        );

        var jogoSalvo = new Jogo();
        jogoSalvo.setId(1L);
        jogoSalvo.setNome("Elden Ring");
        jogoSalvo.setGenero(Genero.RPG);
        jogoSalvo.setPlataformas(Set.of(Plataforma.PC, Plataforma.PLAYSTATION_5));
        jogoSalvo.setImageUrl("https://images.igdb.com/igdb/image/upload/t_cover_big/cover.jpg");
        jogoSalvo.setSummary("Resumo do jogo");
        jogoSalvo.setRating(null);
        jogoSalvo.setCreatedAt(LocalDateTime.of(2026, 6, 23, 21, 0));
        jogoSalvo.setUpdatedAt(LocalDateTime.of(2026, 6, 23, 21, 1));

        when(igdbGameSearchClient.buscarPorId(10L)).thenReturn(igdbGame);
        when(jogoService.salvar(any(JogoRequestDto.class))).thenReturn(jogoSalvo);

        igdbImportacaoService.importarJogo(request);

        ArgumentCaptor<JogoRequestDto> captor = ArgumentCaptor.forClass(JogoRequestDto.class);
        verify(jogoService).salvar(captor.capture());

        JogoRequestDto enviado = captor.getValue();

        assertThat(enviado.imageUrl())
                .isEqualTo("https://images.igdb.com/igdb/image/upload/t_cover_big/cover.jpg");
        assertThat(enviado.summary()).isEqualTo("Resumo do jogo");
        assertThat(enviado.rating()).isNull();
    }

    @Test
    @DisplayName("importarJogo deve enviar summary nula quando jogo vier com summary em branco")
    void importarJogo_deveEnviarSummaryNula_quandoJogoVierComSummaryEmBranco() {
        var request = new IgdbImportacaoRequestDto(10L, Genero.RPG);

        var igdbGame = new IgdbGameDto(
                10L,
                "Elden Ring",
                "   ",
                1672444800L,
                96.0,
                new IgdbGameDto.IgdbCoverDto("//images.igdb.com/igdb/image/upload/t_thumb/cover.jpg"),
                List.of(
                        new IgdbGameDto.IgdbPlatformDto("PC"),
                        new IgdbGameDto.IgdbPlatformDto("PlayStation 5")
                )
        );

        var jogoSalvo = new Jogo();
        jogoSalvo.setId(1L);
        jogoSalvo.setNome("Elden Ring");
        jogoSalvo.setGenero(Genero.RPG);
        jogoSalvo.setPlataformas(Set.of(Plataforma.PC, Plataforma.PLAYSTATION_5));
        jogoSalvo.setImageUrl("https://images.igdb.com/igdb/image/upload/t_cover_big/cover.jpg");
        jogoSalvo.setSummary(null);
        jogoSalvo.setRating(BigDecimal.valueOf(96.0));
        jogoSalvo.setCreatedAt(LocalDateTime.of(2026, 6, 23, 21, 0));
        jogoSalvo.setUpdatedAt(LocalDateTime.of(2026, 6, 23, 21, 1));

        when(igdbGameSearchClient.buscarPorId(10L)).thenReturn(igdbGame);
        when(jogoService.salvar(any(JogoRequestDto.class))).thenReturn(jogoSalvo);

        igdbImportacaoService.importarJogo(request);

        ArgumentCaptor<JogoRequestDto> captor = ArgumentCaptor.forClass(JogoRequestDto.class);
        verify(jogoService).salvar(captor.capture());

        JogoRequestDto enviado = captor.getValue();

        assertThat(enviado.imageUrl())
                .isEqualTo("https://images.igdb.com/igdb/image/upload/t_cover_big/cover.jpg");
        assertThat(enviado.summary()).isNull();
        assertThat(enviado.rating()).isEqualByComparingTo("96.00");
    }
}