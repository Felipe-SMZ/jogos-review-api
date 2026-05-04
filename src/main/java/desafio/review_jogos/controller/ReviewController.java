package desafio.review_jogos.controller;

import desafio.review_jogos.model.Usuario;
import desafio.review_jogos.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Reviews", description = "Gerenciamento de avaliações")
@RequestMapping("/reviews")
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Operation(summary = "Remover review por ID")
    @ApiResponse(responseCode = "204", description = "Review removida com sucesso")
    @ApiResponse(responseCode = "403", description = "Sem permissão para deletar esta review")
    @ApiResponse(responseCode = "404", description = "Review não encontrada")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id,
                                        @AuthenticationPrincipal Usuario usuarioAutenticado) {
        reviewService.deletar(id, usuarioAutenticado);
        return ResponseEntity.noContent().build();
    }
}