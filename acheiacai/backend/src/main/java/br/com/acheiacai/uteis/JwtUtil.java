package br.com.acheiacai.uteis;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

public class JwtUtil {

    private static final String CHAVE_SECRETA = System.getenv("JWT_KEY");

    private static final long TEMPO_DE_EXPIRACAO_EM_HORAS = 8;

    static {
        if (CHAVE_SECRETA == null || CHAVE_SECRETA.isBlank()) {
            System.err.println("!!! ERRO CRÍTICO: A variável de ambiente JWT_SECRET_KEY não está definida! !!!");
            throw new RuntimeException("A variável de ambiente JWT_SECRET_KEY não está definida!");
        }
    }

    public static String gerarToken(String nomeUsuario, String funcao) {
        // Converte a nossa string de chave secreta num objeto SecretKey, que é o que a biblioteca usa
        SecretKey chave = Keys.hmacShaKeyFor(CHAVE_SECRETA.getBytes(StandardCharsets.UTF_8));

        Instant agora = Instant.now();

        return Jwts.builder()
                .setSubject(nomeUsuario) // "subject": a quem o token pertence
                .addClaims(Map.of("funcao", funcao))
                .setIssuer("AcheiAcaiAPI") // "issuer": quem emitiu o token
                .setIssuedAt(Date.from(agora)) // "issued at": quando foi emitido
                .setExpiration(Date.from(agora.plus(TEMPO_DE_EXPIRACAO_EM_HORAS, ChronoUnit.HOURS))) // "expiration": quando expira
                .signWith(chave) // Assina o token com a nossa chave secreta
                .compact(); // Constrói a string final do token
    }

    /**
     * Valida um token e extrai o nome do utilizador (o "subject").
     *
     * @param token A string do token JWT recebida do cliente.
     * @return O nome do utilizador se o token for válido, ou null se for inválido/expirado.
     */
    public static Claims extrairTodasAsClaims(String token) {

        SecretKey chave = Keys.hmacShaKeyFor(CHAVE_SECRETA.getBytes(StandardCharsets.UTF_8));

        try {
            return Jwts.parser()
                    .setSigningKey(chave)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (ExpiredJwtException e) {
            System.err.println("Token JWT expirado: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.err.println("Token JWT mal formatado: " + e.getMessage());
        } catch (SignatureException e) {
            System.err.println("Assinatura do token JWT inválida: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro ao validar o token JWT: " + e.getMessage());
        }

        return null;
    }
}
