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
 * Carga datos iniciales al arrancar la aplicación.
 *
 * Crea dos usuarios de prueba si no existen:
 *   Admin : admin@eventos.com / 1234567890  → puede usar todos los endpoints
 *   User  : user@eventos.com  / 1234567890  → solo puede ver el catálogo
 *
 * Ambos quedan con emailVerificado = true para poder hacer login de inmediato.
 *
 * Para probar en Swagger:
 *   1. POST /api/auth/login con admin@eventos.com / 1234567890
 *   2. Copia el token de la respuesta
 *   3. Clic en "Authorize" → pega el token → ya puedes usar todos los endpoints
 */
@Configuration
public class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepo, PasswordEncoder passwordEncoder) {
        return args -> {

            // --- Admin ---
            Optional<Usuario> adminExistente = usuarioRepo.findByEmail("admin@eventos.com");
            if (adminExistente.isPresent()) {
                log.info("Admin ya existe, omitiendo creación...");
            } else {
                Usuario admin = new Usuario();
                admin.setNombre("Administrador");
                admin.setEmail("admin@eventos.com");
                admin.setContrasenaHash(passwordEncoder.encode("1234567890"));
                admin.setRol(Rol.ADMIN);
                admin.setActivo(true);
                admin.setEmailVerificado(true);
                usuarioRepo.save(admin);
                log.info("✓ Admin creado → admin@eventos.com / 1234567890");
            }

            // --- Usuario normal ---
            Optional<Usuario> userExistente = usuarioRepo.findByEmail("user@eventos.com");
            if (userExistente.isPresent()) {
                log.info("Usuario normal ya existe, omitiendo creación...");
            } else {
                Usuario user = new Usuario();
                user.setNombre("Usuario Normal");
                user.setEmail("user@eventos.com");
                user.setContrasenaHash(passwordEncoder.encode("1234567890"));
                user.setRol(Rol.USER);
                user.setActivo(true);
                user.setEmailVerificado(true);
                usuarioRepo.save(user);
                log.info("✓ Usuario creado → user@eventos.com / 1234567890");
            }
        };
    }
}
