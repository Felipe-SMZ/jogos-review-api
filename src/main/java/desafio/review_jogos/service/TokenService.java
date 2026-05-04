package desafio.review_jogos.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import desafio.review_jogos.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    private static final long EXPIRACAO_HORAS = 2;

    public String gerarToken(Usuario usuario) {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
                .withIssuer("review-jogos-api")       // identifica quem emitiu o token
                .withSubject(usuario.getEmail())        // "dono" do token
                .withClaim("role", usuario.getRole().name()) // dado extra no payload
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
        return LocalDateTime.now()
                .plusHours(EXPIRACAO_HORAS)
                .toInstant(ZoneOffset.of("-03:00"));
    }
}
