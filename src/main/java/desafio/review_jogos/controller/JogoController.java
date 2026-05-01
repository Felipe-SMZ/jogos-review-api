package desafio.review_jogos.controller;

import desafio.review_jogos.dto.JogoResponseDto;
import desafio.review_jogos.dto.MediaNotasResponseDto;
import desafio.review_jogos.dto.ReviewRequestDto;
import desafio.review_jogos.dto.ReviewResponseDto;
import desafio.review_jogos.mapper.JogoMapper;
import desafio.review_jogos.mapper.ReviewMapper;
import desafio.review_jogos.model.Jogo;
import desafio.review_jogos.model.Review;
import desafio.review_jogos.service.JogoService;
import desafio.review_jogos.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jogos")
public class JogoController {

    private final JogoService jogoService;
    private final ReviewService reviewService;

    public JogoController(JogoService jogoService, ReviewService reviewService) {
        this.jogoService = jogoService;
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<JogoResponseDto> inserirJogo(@Validated @RequestBody Jogo jogo) {
        Jogo jogoCadastrado = jogoService.salvar(jogo);
        return ResponseEntity.ok(JogoMapper.toResponse(jogoCadastrado));
    }

    @GetMapping
    public ResponseEntity<List<JogoResponseDto>> buscarTodos() {
        return ResponseEntity.ok(jogoService.buscarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JogoResponseDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(jogoService.buscarPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        jogoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    //Reviews do jogo
    @PostMapping("/{id}/reviews")
    public ResponseEntity<ReviewResponseDto> inserirReview(
            @PathVariable Long id,
            @Validated @RequestBody ReviewRequestDto dto) {

        // Passamos o ID da URL e o DTO para o Service
        ReviewResponseDto response = reviewService.salvar(id, dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<ReviewResponseDto>> buscarTodosReviewsPorJogo(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.listar(id));
    }

    @GetMapping("/{id}/media")
    public ResponseEntity<MediaNotasResponseDto> mediaNotaPorJogo(@PathVariable Long id) {
        return ResponseEntity.ok(jogoService.buscarMediaDoJogo(id));
    }
}

