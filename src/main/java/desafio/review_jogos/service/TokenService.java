package desafio.review_jogos.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import desafio.review_jogos.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

import java.time.Instant;

@Service
public class TokenService {

    private static final Duration EXPIRACAO = Duration.ofHours(2);

    @Value("${api.security.token.secret}")
    private String secret;

    public String gerarToken(Usuario usuario) {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
                .withIssuer("review-jogos-api")
                .withSubject(usuario.getEmail())
                .withClaim("id", usuario.getId())
                .withClaim("role", usuario.getRole().name())
                .withClaim("nickname", usuario.getNickname())
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
        return Instant.now().plus(EXPIRACAO);
    }
}
