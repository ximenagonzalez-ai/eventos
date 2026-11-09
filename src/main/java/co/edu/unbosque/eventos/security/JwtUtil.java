package co.edu.unbosque.eventos.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import co.edu.unbosque.eventos.model.Usuario;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utilidad para crear y validar los tokens JWT de nuestro proyecto.
 *
 * ¿Qué es un JWT?
 * Es una cadena de texto codificada (ej: "eyJhbGciOiJIUzI1NiJ9...") que guarda
 * dentro: el email del usuario, su rol y cuándo vence. El servidor NO guarda la
 * sesión — solo verifica que el token sea válido cada vez que llega una petición.
 *
 * Flujo:
 *   1. El usuario hace login → generateToken() crea el token
 *   2. El frontend guarda ese token y lo envía en cada petición: "Authorization: Bearer <token>"
 *   3. JwtAuthenticationFilter intercepta la petición, llama a validateToken()
 *   4. Si es válido, Spring Security autoriza la petición
 *
 * La clave secreta (jwt.secret en application.properties) debe tener al menos 32 caracteres.
 * Nunca la subas al repositorio — usa variables de entorno en producción.
 *
 * Los tokens duran 24 horas (JWT_TOKEN_VALIDITY). Después hay que hacer login de nuevo.
 */
@Component
public class JwtUtil {

    /** Duración del token: 24 horas en milisegundos. */
    private static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60 * 1000;

    @Value("${jwt.secret:secureJwtSecretKeyForSpringFirstAppJWTImplementation}")
    private String secret;

    /** Convierte el string secreto en una clave criptográfica para firmar el token. */
    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /** Extrae el email del usuario guardado dentro del token. */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /** Extrae la fecha de vencimiento del token. */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /** Método genérico para extraer cualquier dato (claim) del token. */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /** Parsea el token y devuelve todos sus datos internos. */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Crea un nuevo token JWT para el usuario.
     * Incluye dentro: sus authorities (roles) y su rol como string para que el
     * frontend pueda leerlo fácilmente y mostrar/ocultar secciones.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", userDetails.getAuthorities());
        if (userDetails instanceof Usuario u) {
            claims.put("role", u.getRol().name()); // "USER" o "ADMIN"
        }
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Verifica que el token sea válido: el email debe coincidir con el usuario
     * y el token no debe estar vencido.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
