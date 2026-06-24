package desafio.review_jogos.service;

import desafio.review_jogos.client.IgdbGameSearchClient;
import desafio.review_jogos.client.dto.IgdbGameDto;
import desafio.review_jogos.dto.IgdbImportacaoRequestDto;
import desafio.review_jogos.dto.JogoRequestDto;
import desafio.review_jogos.dto.JogoResponseDto;
import desafio.review_jogos.mapper.JogoMapper;
import desafio.review_jogos.model.Jogo;
import desafio.review_jogos.model.enums.Plataforma;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

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
            imageUrl = normalizarCoverUrl(jogoIgdb.cover().url())
                    .replace("t_thumb", "t_cover_big");
        }

        String summary = null;
        if (jogoIgdb.summary() != null && !jogoIgdb.summary().isBlank()) {
            summary = jogoIgdb.summary().trim();
        }

        BigDecimal rating = null;
        if (jogoIgdb.rating() != null) {
            rating = BigDecimal.valueOf(jogoIgdb.rating())
                    .setScale(2, RoundingMode.HALF_UP);
        }

        Set<Plataforma> plataformas = mapearPlataformasIgdb(jogoIgdb);

        return new JogoRequestDto(
                null,
                jogoIgdb.name(),
                dto.genero(),
                plataformas,
                imageUrl,
                summary,
                rating
        );
    }

    private String normalizarCoverUrl(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }

        String urlLimpa = url.trim();

        if (urlLimpa.startsWith("https://") || urlLimpa.startsWith("http://")) {
            return urlLimpa;
        }

        if (urlLimpa.startsWith("//")) {
            return "https:" + urlLimpa;
        }

        return "https://" + urlLimpa;
    }

    private Set<Plataforma> mapearPlataformasIgdb(IgdbGameDto jogoIgdb) {
        Set<Plataforma> plataformas = new LinkedHashSet<>();

        if (jogoIgdb.platforms() == null || jogoIgdb.platforms().isEmpty()) {
            return plataformas;
        }

        for (IgdbGameDto.IgdbPlatformDto platformDto : jogoIgdb.platforms()) {
            if (platformDto == null || platformDto.name() == null || platformDto.name().isBlank()) {
                continue;
            }

            plataformas.add(mapearNomePlataforma(platformDto.name()));
        }

        return plataformas;
    }

    private Plataforma mapearNomePlataforma(String nomePlataformaIgdb) {
        String nomeNormalizado = nomePlataformaIgdb.trim().toLowerCase(Locale.ROOT);

        return switch (nomeNormalizado) {
            case "pc (microsoft windows)", "pc", "windows", "mac", "linux" -> Plataforma.PC;

            case "playstation", "ps1" -> Plataforma.PLAYSTATION;
            case "playstation 2", "ps2" -> Plataforma.PLAYSTATION_2;
            case "playstation 3", "ps3" -> Plataforma.PLAYSTATION_3;
            case "playstation 4", "ps4" -> Plataforma.PLAYSTATION_4;
            case "playstation 5", "ps5" -> Plataforma.PLAYSTATION_5;
            case "playstation portable", "psp" -> Plataforma.PLAYSTATION_PORTABLE;
            case "playstation vita", "ps vita", "vita" -> Plataforma.PLAYSTATION_VITA;

            case "xbox" -> Plataforma.XBOX;
            case "xbox 360" -> Plataforma.XBOX_360;
            case "xbox one" -> Plataforma.XBOX_ONE;
            case "xbox series x" -> Plataforma.XBOX_SERIES_X;
            case "xbox series s" -> Plataforma.XBOX_SERIES_S;
            case "xbox series x|s" -> Plataforma.XBOX_SERIES_X;

            case "nintendo entertainment system", "nes" -> Plataforma.NINTENDO_ENTERTAINMENT_SYSTEM;
            case "super nintendo entertainment system", "super nintendo", "snes" -> Plataforma.SUPER_NINTENDO;
            case "nintendo 64", "n64" -> Plataforma.NINTENDO_64;
            case "gamecube", "nintendo gamecube" -> Plataforma.NINTENDO_GAMECUBE;
            case "wii", "nintendo wii" -> Plataforma.NINTENDO_WII;
            case "wii u", "nintendo wii u" -> Plataforma.NINTENDO_WII_U;
            case "nintendo switch", "switch" -> Plataforma.NINTENDO_SWITCH;
            case "game boy", "gb" -> Plataforma.GAME_BOY;
            case "game boy advance", "gba" -> Plataforma.GAME_BOY_ADVANCE;
            case "nintendo ds", "ds" -> Plataforma.NINTENDO_DS;
            case "nintendo 3ds", "3ds" -> Plataforma.NINTENDO_3DS;

            case "android", "ios" -> Plataforma.MOBILE;

            default -> Plataforma.OUTROS;
        };
    }
}