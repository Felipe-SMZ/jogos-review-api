package desafio.review_jogos.controller;

import desafio.review_jogos.dto.JogoResponseDto;
import desafio.review_jogos.mapper.JogoMapper;
import desafio.review_jogos.model.Jogo;
import desafio.review_jogos.service.JogoService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jogos")
public class JogoController {

    private final JogoService jogoService;

    public JogoController(JogoService jogoService) {
        this.jogoService = jogoService;
    }

    @PostMapping
    public ResponseEntity<JogoResponseDto> inserirJogo(@Validated @RequestBody Jogo jogo) {
        Jogo jogoCadastrado = jogoService.salvar(jogo);
        return ResponseEntity.ok(JogoMapper.toResponse(jogoCadastrado));
    }
}
