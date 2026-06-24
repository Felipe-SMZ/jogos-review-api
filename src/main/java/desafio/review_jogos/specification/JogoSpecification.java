package desafio.review_jogos.specification;

import desafio.review_jogos.model.Jogo;
import desafio.review_jogos.model.enums.Genero;
import desafio.review_jogos.model.enums.Plataforma;
import org.springframework.data.jpa.domain.Specification;

public class JogoSpecification {

    public static Specification<Jogo> porGenero(Genero genero) {
        return (root, query, cb) ->
                genero == null ? cb.conjunction() : cb.equal(root.get("genero"), genero);
    }

    public static Specification<Jogo> porPlataforma(Plataforma plataforma) {
        return (root, query, cb) -> {
            if (plataforma == null) {
                return cb.conjunction();
            }

            query.distinct(true);
            return cb.equal(root.join("plataformas"), plataforma);
        };
    }
}