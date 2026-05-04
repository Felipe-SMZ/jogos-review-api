package desafio.review_jogos.controller;

import desafio.review_jogos.dto.ReviewRequestDto;
import desafio.review_jogos.dto.ReviewResponseDto;
import desafio.review_jogos.model.Usuario;
import desafio.review_jogos.service.ReviewService;
import desafio.review_jogos.validation.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "Atualizar review por ID")
    @ApiResponse(responseCode = "200", description = "Review atualizada com sucesso")
    @ApiResponse(responseCode = "403", description = "Sem permissão para editar esta review")
    @ApiResponse(responseCode = "404", description = "Review não encontrada")
    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> atualizar(
            @PathVariable Long id,
            @Validated(OnUpdate.class) @RequestBody ReviewRequestDto dto,
            @AuthenticationPrincipal Usuario usuarioAutenticado) {

        return ResponseEntity.ok(reviewService.atualizar(id, dto, usuarioAutenticado));
    }
}