package co.edu.unbosque.eventos.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que se ejecuta UNA VEZ por cada petición HTTP que llega al servidor.
 *
 * Su trabajo es leer el token JWT del header "Authorization", validarlo,
 * y si es válido, decirle a Spring Security quién es el usuario autenticado.
 *
 * Flujo por cada petición:
 *   1. Lee el header: "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
 *   2. Extrae el email del token con JwtUtil
 *   3. Busca el usuario en la BD con UserDetailsServiceImpl
 *   4. Valida que el token no haya vencido y corresponda a ese usuario
 *   5. Registra la autenticación en el SecurityContext para que Spring Security
 *      sepa que esta petición está autorizada
 *   6. Continúa la cadena de filtros (pasa la petición al controlador)
 *
 * Si el header no existe o el token es inválido, no se autentica nada
 * y Spring Security rechazará la petición si la ruta lo requiere.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Solo procesamos si el header empieza con "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7); // quitamos "Bearer " y nos queda solo el token
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                // Token malformado o vencido — no autenticamos, seguimos la cadena
                logger.error("Error extrayendo username del token: " + e.getMessage());
            }
        }

        // Solo autenticamos si tenemos el email y aún no hay autenticación en el contexto
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(jwt, userDetails)) {
                // Token válido: registramos al usuario en el contexto de seguridad
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response); // continúa al siguiente filtro/controlador
    }
}
