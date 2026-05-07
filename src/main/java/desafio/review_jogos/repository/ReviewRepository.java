package desafio.review_jogos.repository;

import desafio.review_jogos.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByJogoId(Long jogoId);

    Page<Review> findByJogoId(Long jogoId, Pageable pageable);

    boolean existsByJogoIdAndUsuarioId(Long jogoId, Long usuarioId);

    @Query("""
            SELECT AVG(r.nota)
            FROM Review r
            WHERE r.jogo.id = :jogoId
            """)
    Double calcularMediaPorJogoId(@Param("jogoId") Long jogoId);

    long countByJogoId(Long jogoId);
}
