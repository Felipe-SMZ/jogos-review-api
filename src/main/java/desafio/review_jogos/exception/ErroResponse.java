package desafio.review_jogos.exception;

import java.time.LocalDateTime;

public class ErroResponse {
    private int status;
    private String erro;
    private String message;
    private LocalDateTime timestamp;

    public ErroResponse(int status, String erro, String message) {
        this.status = status;
        this.erro = erro;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatus() {
        return status;
    }

    public String getErro() {
        return erro;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
