package desafio.review_jogos.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import desafio.review_jogos.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    private static final long EXPIRACAO_HORAS = 2;

    public String gerarToken(Usuario usuario) {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
                .withIssuer("review-jogos-api")
                .withSubject(usuario.getEmail())
                .withClaim("id", usuario.getId())
                .withClaim("role", usuario.getRole().name())
                .withExpiresAt(dataExpiracao())
                .sign(algorithm);
    }

    public String validarToken(String token) {
        // Retorna o subject (email) se válido, ou lança exceção se inválido/expirado
        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.require(algorithm)
                .withIssuer("review-jogos-api")
                .build()
                .verify(token)          // ← lança JWTVerificationException se inválido
                .getSubject();

    }

    private Instant dataExpiracao() {
        return Instant.now().plusSeconds(EXPIRACAO_HORAS * 3600);
    }
}
