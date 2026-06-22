package desafio.review_jogos.controller;

import desafio.review_jogos.client.IgdbGameSearchClient;
import desafio.review_jogos.client.dto.IgdbGameDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teste-igdb")
public class IgdbTestController {


    private final IgdbGameSearchClient igdbGameSearchClient;

    public IgdbTestController(IgdbGameSearchClient igdbGameSearchClient) {
        this.igdbGameSearchClient = igdbGameSearchClient;
    }

    @GetMapping
    public ResponseEntity<IgdbGameDto> buscarPorId(@RequestParam Long igdbId) {
        return ResponseEntity.ok(
                igdbGameSearchClient.buscarPorId(igdbId));
    }
}
