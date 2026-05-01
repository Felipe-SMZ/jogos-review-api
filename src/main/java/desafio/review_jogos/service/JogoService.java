package desafio.review_jogos.service;

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

    public List<Jogo> buscarTodos() {
        return jogoRepository.findAll();
    }

    public Optional<Jogo> buscarPorId(Long id) {
        if (jogoRepository.existsById(id)) {
            return jogoRepository.findById(id);
        } else {
            throw new RuntimeException("Jogo com id " + id + " não encontrado.");
        }

    }

}
