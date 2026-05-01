package desafio.review_jogos.service;

import desafio.review_jogos.repository.JogoRepository;
import org.springframework.stereotype.Service;

@Service
public class JogoService {

    private JogoRepository jogoRepository;

    public JogoService(JogoRepository jogoRepository) {
        this.jogoRepository = jogoRepository;
    }


}
