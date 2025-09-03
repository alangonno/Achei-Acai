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

public class JwtUtil {

    private static final String CHAVE_SECRETA ="lUsOBsE+YfHxS15VTRXp09UENL9QRqqGZGETKncZZu+u9OwCa+KHUd3M0A/1w967";

    private static final long TEMPO_DE_EXPIRACAO_EM_HORAS = 8;

    public static String gerarToken(String nomeUsuario) {
        // Converte a nossa string de chave secreta num objeto SecretKey, que é o que a biblioteca usa
        SecretKey chave = Keys.hmacShaKeyFor(CHAVE_SECRETA.getBytes(StandardCharsets.UTF_8));

        Instant agora = Instant.now();

        return Jwts.builder()
                .setSubject(nomeUsuario) // "subject": a quem o token pertence
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
    public static String extrairNomeUsuario(String token) {
        // Converte a nossa chave para o mesmo formato usado na geração
        SecretKey chave = Keys.hmacShaKeyFor(CHAVE_SECRETA.getBytes(StandardCharsets.UTF_8));

        try {
            // Tenta "parsear" o token. A biblioteca irá verificar a assinatura e a data de expiração.
            // Se algo estiver errado, ela irá lançar uma exceção.
            Claims claims = Jwts.parser()
                    .setSigningKey(chave)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Se chegou até aqui, o token é válido.
            return claims.getSubject();

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
