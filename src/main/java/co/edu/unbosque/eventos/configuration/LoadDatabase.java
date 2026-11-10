package co.edu.unbosque.eventos.configuration;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import co.edu.unbosque.eventos.model.Rol;
import co.edu.unbosque.eventos.model.Usuario;
import co.edu.unbosque.eventos.repository.UsuarioRepository;

/**
 * Datos iniciales que se cargan automáticamente al arrancar la aplicación.
 *
 * Crea dos usuarios de prueba si no existen en la BD:
 *
 *   Admin:
 *     Email:     admin@eventos.com
 *     Contraseña: 1234567890
 *     Rol:       ADMIN (puede hacer todo en Swagger)
 *
 *   Usuario normal:
 *     Email:     user@eventos.com
 *     Contraseña: 1234567890
 *     Rol:       USER (solo puede ver el catálogo)
 *
 * Ambos se crean con emailVerificado = true para poder hacer login de inmediato
 * sin pasar por el flujo de verificación de correo.
 *
 * Flujo de prueba en Swagger:
 *   1. POST /api/auth/login con admin@eventos.com / 1234567890
 *   2. Copia el token de la respuesta
 *   3. Haz clic en "Authorize" (arriba a la derecha) y pega el token
 *   4. Ya puedes usar todos los endpoints
 */
@Configuration
public class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepo, PasswordEncoder passwordEncoder) {
        return args -> {

            // --- Crear admin si no existe ---
            Optional<Usuario> adminExistente = usuarioRepo.findByEmail("admin@eventos.com");
            if (adminExistente.isPresent()) {
                log.info("Admin ya existe, omitiendo creación...");
            } else {
                Usuario admin = Usuario.builder()
                        .nombre("Administrador")
                        .email("admin@eventos.com")
                        .contrasenaHash(passwordEncoder.encode("1234567890"))
                        .rol(Rol.ADMIN)
                        .activo(true)
                        .emailVerificado(true) // sin esto no puede hacer login
                        .build();
                usuarioRepo.save(admin);
                log.info("✓ Admin creado → admin@eventos.com / 1234567890");
            }

            // --- Crear usuario normal si no existe ---
            Optional<Usuario> userExistente = usuarioRepo.findByEmail("user@eventos.com");
            if (userExistente.isPresent()) {
                log.info("Usuario normal ya existe, omitiendo creación...");
            } else {
                Usuario user = Usuario.builder()
                        .nombre("Usuario Normal")
                        .email("user@eventos.com")
                        .contrasenaHash(passwordEncoder.encode("1234567890"))
                        .rol(Rol.USER)
                        .activo(true)
                        .emailVerificado(true)
                        .build();
                usuarioRepo.save(user);
                log.info("✓ Usuario creado → user@eventos.com / 1234567890");
            }
        };
    }
}
