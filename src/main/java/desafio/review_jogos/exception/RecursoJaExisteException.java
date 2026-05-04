package desafio.review_jogos.exception;

public class RecursoJaExisteException extends RuntimeException {
    public RecursoJaExisteException(String messagem) {
        super(messagem);
    }
}
