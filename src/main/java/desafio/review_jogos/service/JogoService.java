package desafio.review_jogos.service;

import desafio.review_jogos.dto.JogoResponseDto;
import desafio.review_jogos.mapper.JogoMapper;
import desafio.review_jogos.model.Jogo;
import desafio.review_jogos.repository.JogoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JogoService {

    private final JogoRepository jogoRepository;

    public JogoService(JogoRepository jogoRepository) {
        this.jogoRepository = jogoRepository;
    }

    public Jogo salvar(Jogo jogo) {
        //o trim para tirar espacos extras
        String nomeLimpo = jogo.getNome().trim();

        if (jogoRepository.existsByNomeIgnoreCase(nomeLimpo)) {
            throw new RuntimeException("Não foi possível cadastrar: O jogo '" + nomeLimpo + "' já existe.");
        }

        jogo.setNome(nomeLimpo);
        return jogoRepository.save(jogo);
    }

    public List<JogoResponseDto> buscarTodos() {
        List <Jogo> jogos = jogoRepository.findAll();
        return jogos.stream()
                .map(JogoMapper::toResponse)
                .toList();
    }

    public JogoResponseDto buscarPorId(Long id) {
        return jogoRepository.findById(id)
                .map(JogoMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Jogo com id " + id + " não encontrado."));
    }

    public void excluir(Long id) {
        Jogo jogoExiste = jogoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Não foi possível excluir: Jogo com id " + id + " não encontrado."));

        jogoRepository.delete(jogoExiste);
    }

}
