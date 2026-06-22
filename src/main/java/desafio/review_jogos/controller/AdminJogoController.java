package desafio.review_jogos.controller;

import desafio.review_jogos.client.IgdbGameSearchClient;
import desafio.review_jogos.client.dto.IgdbGameDto;
import desafio.review_jogos.dto.IgdbImportacaoRequestDto;
import desafio.review_jogos.dto.JogoResponseDto;
import desafio.review_jogos.service.IgdbImportacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin - Jogos", description = "Operações administrativas de jogos via integração IGDB")
@RestController
@RequestMapping("/admin/jogos")
public class AdminJogoController {

    private final IgdbImportacaoService igdbImportacaoService;
    private final IgdbGameSearchClient igdbGameSearchClient;

    public AdminJogoController(IgdbImportacaoService igdbImportacaoService, IgdbGameSearchClient igdbGameSearchClient) {
        this.igdbImportacaoService = igdbImportacaoService;
        this.igdbGameSearchClient = igdbGameSearchClient;
    }

    @Operation(summary = "Buscar jogos na IGDB por termo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de jogos retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Termo de busca inválido ou ausente"),
            @ApiResponse(responseCode = "502", description = "Falha na comunicação com a IGDB")
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<IgdbGameDto>> buscarJogos(@RequestParam String termoBusca) {
        return ResponseEntity.ok(igdbGameSearchClient.buscarJogos(termoBusca));
    }

    @Operation(summary = "Importar jogo da IGDB para o sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Jogo importado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou ausentes"),
            @ApiResponse(responseCode = "404", description = "Jogo não encontrado na IGDB"),
            @ApiResponse(responseCode = "409", description = "Jogo já existe no sistema"),
            @ApiResponse(responseCode = "502", description = "Falha na comunicação com a IGDB")
    })
    @PostMapping("/importar")
    public ResponseEntity<JogoResponseDto> importarJogo(@Validated @RequestBody IgdbImportacaoRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(igdbImportacaoService.importarJogo(dto));
    }
}
