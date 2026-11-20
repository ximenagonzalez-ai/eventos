
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

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                          UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // Rutas publicas
                .requestMatchers(
                    "/api/auth/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/api/rutas/**"
                ).permitAll()

                // Catalogo de eventos publico
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

                // Gestion de eventos solo ADMIN
                .requestMatchers("/evento/**").hasRole("ADMIN")

                // Consultar categorias es publico
                .requestMatchers(
                    "/categoria/getall",
                    "/categoria/getbyid/**"
                ).permitAll()

                // Gestion de categorias solo ADMIN
                .requestMatchers("/categoria/**").hasRole("ADMIN")

                // Consultar lugares es publico
                .requestMatchers(
                    "/lugar/getall",
                    "/lugar/getbyid/**",
                    "/lugar/bycity"
                ).permitAll()

                // Gestion de lugares solo ADMIN
                .requestMatchers("/lugar/**").hasRole("ADMIN")

                // Preferencias de usuarios autenticados
                .requestMatchers("/api/preferencias/**")
                    .hasAnyRole("USER", "ADMIN")

                // Usuarios
                .requestMatchers(
                    "/user/getall",
                    "/user/getbyid/**",
                    "/user/exists/**",
                    "/user/count"
                ).hasAnyRole("USER", "ADMIN")

                // Gestion de usuarios solo ADMIN
                .requestMatchers("/user/**").hasRole("ADMIN")

                // Auditoria solo ADMIN
                .requestMatchers("/auditoria/**").hasRole("ADMIN")

                // Todo lo demas requiere autenticacion
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Value("${app.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(frontendUrl));
        config.setAllowedMethods(List.of(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
