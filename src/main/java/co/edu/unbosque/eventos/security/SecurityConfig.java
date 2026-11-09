package co.edu.unbosque.eventos.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuración central de seguridad de nuestro proyecto.
 *
 * Aquí definimos:
 *   - Qué rutas son públicas (Swagger, /api/auth/**)
 *   - Qué rutas necesitan rol USER o ADMIN
 *   - Qué rutas son solo para ADMIN
 *   - Que usamos JWT (sin sesiones en servidor)
 *   - El proveedor de autenticación (busca usuario por email en la BD y compara con BCrypt)
 *   - CORS: solo el frontend configurado en app.frontend-url puede hacer peticiones
 *
 * El filtro JWT (JwtAuthenticationFilter) se ejecuta antes de cualquier otro filtro
 * de Spring Security, lee el token del header "Authorization: Bearer <token>",
 * lo valida y setea la autenticación en el contexto.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                          UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource(null)))
            .csrf(csrf -> csrf.disable()) // Deshabilitado porque usamos JWT, no cookies de sesión
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Sin sesiones en servidor
            .authorizeHttpRequests(auth -> auth

                // ---- Rutas públicas (no necesitan token) ----
                .requestMatchers(
                    "/api/auth/**",      // registro, login, verificación de email
                    "/swagger-ui/**",    // documentación de la API
                    "/swagger-ui.html",
                    "/v3/api-docs/**"
                ).permitAll()

                // ---- Catálogo de eventos: cualquier usuario autenticado puede ver ----
                .requestMatchers(
                    "/evento/getall", "/evento/getbyid/**", "/evento/bycategoria/**",
                    "/evento/byestado", "/evento/bycity", "/evento/search",
                    "/evento/byfecha", "/evento/count"
                ).hasAnyRole("USER", "ADMIN")

                // ---- Gestión de eventos (crear, editar, borrar, sync Ticketmaster): solo ADMIN ----
                .requestMatchers("/evento/**").hasRole("ADMIN")

                // ---- Categorías: ver = USER y ADMIN, crear/editar/borrar = solo ADMIN ----
                .requestMatchers("/categoria/getall", "/categoria/getbyid/**")
                    .hasAnyRole("USER", "ADMIN")
                .requestMatchers("/categoria/**").hasRole("ADMIN")

                // ---- Lugares: ver = USER y ADMIN, crear/editar/borrar = solo ADMIN ----
                .requestMatchers("/lugar/getall", "/lugar/getbyid/**", "/lugar/bycity")
                    .hasAnyRole("USER", "ADMIN")
                .requestMatchers("/lugar/**").hasRole("ADMIN")

                // ---- Usuarios: ver info = USER y ADMIN, crear/editar/borrar = solo ADMIN ----
                .requestMatchers("/user/getall", "/user/getbyid/**", "/user/exists/**", "/user/count")
                    .hasAnyRole("USER", "ADMIN")
                .requestMatchers("/user/**").hasRole("ADMIN")

                // ---- Auditoría: solo ADMIN (historial de acciones) ----
                .requestMatchers("/auditoria/**").hasRole("ADMIN")

                // ---- Todo lo demás requiere estar autenticado ----
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Le dice a Spring Security cómo verificar las credenciales:
     * busca el usuario por email (UserDetailsServiceImpl) y compara
     * la contraseña con BCrypt.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /** El AuthenticationManager es el que usamos en AuthController para hacer el login. */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    /** BCrypt es el algoritmo que usamos para encriptar contraseñas antes de guardarlas. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuración de CORS: solo permite peticiones desde el frontend configurado.
     * En desarrollo: http://localhost:3000 (ajustable en application.properties con app.frontend-url)
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.frontend-url:http://localhost:3000}") String frontendUrl) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(frontendUrl));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
