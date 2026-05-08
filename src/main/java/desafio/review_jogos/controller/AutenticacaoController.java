package desafio.review_jogos.controller;

import desafio.review_jogos.dto.LoginRequestDto;
import desafio.review_jogos.dto.LoginResponseDto;
import desafio.review_jogos.dto.UsuarioRequestDto;
import desafio.review_jogos.model.Usuario;
import desafio.review_jogos.service.TokenService;
import desafio.review_jogos.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Endpoints para registro e login de usuários")
public class AutenticacaoController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UsuarioService usuarioService;

    public AutenticacaoController(AuthenticationManager authenticationManager,
                                  TokenService tokenService,
                                  UsuarioService usuarioService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.usuarioService = usuarioService;
    }

    @Operation(
            summary = "Autenticar usuário",
            description = "Realiza o login com email e senha e retorna um token JWT"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas",
                    content = @Content(schema = @Schema(implementation = desafio.review_jogos.exception.ErroResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = desafio.review_jogos.exception.ErroResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto dto) {
        var autenticacao = new UsernamePasswordAuthenticationToken(dto.email(), dto.senha());
        var auth = authenticationManager.authenticate(autenticacao);
        var token = tokenService.gerarToken((Usuario) auth.getPrincipal());
        return ResponseEntity.ok(new LoginResponseDto(token));
    }

    @Operation(
            summary = "Registrar usuário",
            description = "Cadastra um novo usuário com role ROLE_USER"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado",
                    content = @Content(schema = @Schema(implementation = desafio.review_jogos.exception.ErroResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = desafio.review_jogos.exception.ErroResponse.class)))
    })
    @PostMapping("/registrar")
    public ResponseEntity<Void> registrar(@RequestBody @Valid UsuarioRequestDto dto) {
        usuarioService.cadastrarUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}