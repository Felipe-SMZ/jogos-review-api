package desafio.review_jogos.repository;

import desafio.review_jogos.model.Jogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface JogoRepository extends JpaRepository<Jogo, Long>, JpaSpecificationExecutor<Jogo> {

    boolean existsByNomeIgnoreCase(String nome);

    long countById(Long id);
}
