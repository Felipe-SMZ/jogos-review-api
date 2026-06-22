package desafio.review_jogos.service;

import desafio.review_jogos.client.IgdbGameSearchClient;
import desafio.review_jogos.client.dto.IgdbGameDto;
import desafio.review_jogos.dto.IgdbImportacaoRequestDto;
import desafio.review_jogos.dto.JogoRequestDto;
import desafio.review_jogos.dto.JogoResponseDto;
import desafio.review_jogos.mapper.JogoMapper;
import desafio.review_jogos.model.Jogo;
import org.springframework.stereotype.Service;

@Service
public class IgdbImportacaoService {

    private final IgdbGameSearchClient igdbGameSearchClient;
    private final JogoService jogoService;

    public IgdbImportacaoService(IgdbGameSearchClient igdbGameSearchClient,
                                 JogoService jogoService) {
        this.igdbGameSearchClient = igdbGameSearchClient;
        this.jogoService = jogoService;
    }

    public JogoResponseDto importarJogo(IgdbImportacaoRequestDto dto) {
        IgdbGameDto jogoIgdb = igdbGameSearchClient.buscarPorId(dto.igdbId());

        JogoRequestDto jogoRequestDto = montarJogoRequest(jogoIgdb, dto);

        Jogo jogoSalvo = jogoService.salvar(jogoRequestDto);

        return JogoMapper.toResponse(jogoSalvo);
    }

    private JogoRequestDto montarJogoRequest(IgdbGameDto jogoIgdb,
                                             IgdbImportacaoRequestDto dto) {
        String imageUrl = null;
        if (jogoIgdb.cover() != null && jogoIgdb.cover().url() != null) {
            imageUrl = "https:" + jogoIgdb.cover().url()
                    .replace("t_thumb", "t_cover_big");
        }

        return new JogoRequestDto(
                null,
                jogoIgdb.name(),
                dto.genero(),
                dto.plataforma(),
                imageUrl
        );
    }
}