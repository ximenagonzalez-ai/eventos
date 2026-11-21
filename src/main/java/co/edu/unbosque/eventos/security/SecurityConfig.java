
package co.edu.unbosque.eventos.security;

import java.util.List;

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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Value("${app.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthFilter,
            UserDetailsService userDetailsService) {

        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth

                // Autenticacion y documentacion publicas
                .requestMatchers(
                    "/api/auth/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/api/rutas/**"
                ).permitAll()

                // Catalogo publico
                .requestMatchers(
                    "/evento/getall",
                    "/evento/getbyid/**",
                    "/evento/bycategoria/**",
                    "/evento/byestado",
                    "/evento/bycity",
                    "/evento/search",
                    "/evento/byfecha",
                    "/evento/count"
                ).permitAll()

                // Administracion de eventos
                .requestMatchers("/evento/**").hasRole("ADMIN")

                // Consultas publicas de categorias
                .requestMatchers(
                    "/categoria/getall",
                    "/categoria/getbyid/**"
                ).permitAll()

                .requestMatchers("/categoria/**").hasRole("ADMIN")

                // Consultas publicas de lugares
                .requestMatchers(
                    "/lugar/getall",
                    "/lugar/getbyid/**",
                    "/lugar/bycity"
                ).permitAll()

                .requestMatchers("/lugar/**").hasRole("ADMIN")

                // Preferencias
                .requestMatchers("/api/preferencias/**")
                    .hasAnyRole("USER", "ADMIN")

                // Usuarios
                .requestMatchers(
                    "/user/getall",
                    "/user/getbyid/**",
                    "/user/exists/**",
                    "/user/count"
                ).hasAnyRole("USER", "ADMIN")

                .requestMatchers("/user/**").hasRole("ADMIN")

                // Auditoria
                .requestMatchers("/auditoria/**").hasRole("ADMIN")

                // Boletas, compras e itinerarios
                .requestMatchers(
                    "/api/boletas/**",
                    "/api/compras/**",
                    "/api/itinerarios/**"
                ).hasAnyRole("USER", "ADMIN")

                // Reportes administrativos
                .requestMatchers("/api/reportes/**").hasRole("ADMIN")

                // Otras peticiones
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(
                jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
            new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {

        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(frontendUrl));

        config.setAllowedMethods(List.of(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
