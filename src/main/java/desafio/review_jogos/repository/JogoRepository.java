package desafio.review_jogos.repository;

import desafio.review_jogos.model.Jogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JogoRepository extends JpaRepository<Jogo, Long> {

    boolean existsByNomeIgnoreCase(String nome);
    boolean existsById(Long id);
}
