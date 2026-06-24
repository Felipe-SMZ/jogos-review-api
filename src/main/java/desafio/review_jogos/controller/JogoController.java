package desafio.review_jogos.controller;

import desafio.review_jogos.dto.JogoRequestDto;
import desafio.review_jogos.dto.JogoResponseDto;
import desafio.review_jogos.dto.MediaNotasResponseDto;
import desafio.review_jogos.dto.ReviewRequestDto;
import desafio.review_jogos.dto.ReviewResponseDto;
import desafio.review_jogos.mapper.JogoMapper;
import desafio.review_jogos.model.Jogo;
import desafio.review_jogos.model.Usuario;
import desafio.review_jogos.model.enums.Genero;
import desafio.review_jogos.model.enums.Plataforma;
import desafio.review_jogos.service.JogoService;
import desafio.review_jogos.service.ReviewService;
import desafio.review_jogos.validation.OnCreate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Jogos", description = "Gerenciamento de jogos")
@RestController
@RequestMapping("/jogos")
public class JogoController {

    private final JogoService jogoService;
    private final ReviewService reviewService;

    public JogoController(JogoService jogoService, ReviewService reviewService) {
        this.jogoService = jogoService;
        this.reviewService = reviewService;
    }

    @Operation(summary = "Criar um novo jogo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Jogo criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<JogoResponseDto> inserirJogo(@Validated @RequestBody JogoRequestDto dto) {
        Jogo jogoCadastrado = jogoService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(JogoMapper.toResponse(jogoCadastrado));
    }

    @Operation(summary = "Listar todos os jogos")
    @ApiResponse(responseCode = "200", description = "Lista de jogos retornada com sucesso")
    @GetMapping
    public ResponseEntity<Page<JogoResponseDto>> buscarTodos(
            @RequestParam(required = false) Genero genero,
            @RequestParam(required = false) Plataforma plataforma,
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {

        return ResponseEntity.ok(jogoService.buscarTodos(genero, plataforma, pageable));
    }

    @Operation(summary = "Buscar jogo por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Jogo encontrado"),
            @ApiResponse(responseCode = "404", description = "Jogo não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<JogoResponseDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(jogoService.buscarPorId(id));
    }

    @Operation(summary = "Remover jogo por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Jogo removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Jogo não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        jogoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualizar jogo por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Jogo atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Jogo não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Já existe um jogo com esse nome")
    })
    @PutMapping("/{id}")
    public ResponseEntity<JogoResponseDto> atualizar(
            @PathVariable Long id,
            @Validated @RequestBody JogoRequestDto dto) {

        return ResponseEntity.ok(jogoService.atualizar(id, dto));
    }

    @Operation(summary = "Criar uma review para um jogo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Review criada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Jogo não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping("/{id}/reviews")
    public ResponseEntity<ReviewResponseDto> inserirReview(
            @PathVariable Long id,
            @Validated(OnCreate.class) @RequestBody ReviewRequestDto dto,
            @AuthenticationPrincipal Usuario usuarioAutenticado) {

        ReviewResponseDto response = reviewService.salvar(id, dto, usuarioAutenticado);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Listar reviews de um jogo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Jogo não encontrado")
    })
    @GetMapping("/{id}/reviews")
    public ResponseEntity<Page<ReviewResponseDto>> buscarTodosReviewsPorJogo(
            @PathVariable Long id,
            @PageableDefault(size = 10, sort = "nota", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(reviewService.listar(id, pageable));
    }

    @Operation(summary = "Calcular média de notas de um jogo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Média calculada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Jogo não encontrado")
    })
    @GetMapping("/{id}/media")
    public ResponseEntity<MediaNotasResponseDto> mediaNotaPorJogo(@PathVariable Long id) {
        return ResponseEntity.ok(jogoService.buscarMediaDoJogo(id));
    }
}