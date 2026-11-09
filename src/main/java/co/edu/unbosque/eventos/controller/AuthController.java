package co.edu.unbosque.eventos.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import co.edu.unbosque.eventos.DTO.LoginRequest;
import co.edu.unbosque.eventos.DTO.RegistroRequest;
import co.edu.unbosque.eventos.DTO.ReenviarVerificacionRequest;
import co.edu.unbosque.eventos.DTO.UsuarioResponse;
import co.edu.unbosque.eventos.model.ResultadoVerificacion;
import co.edu.unbosque.eventos.model.Usuario;
import co.edu.unbosque.eventos.security.JwtUtil;
import co.edu.unbosque.eventos.service.AuthService;

import java.util.Map;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Registro, login y verificación de email")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Operation(summary = "Registrar nuevo usuario",
               description = "Crea la cuenta y envía un correo de verificación. El usuario no puede hacer login hasta verificar su email.")
    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegistroRequest request) {
        Optional<UsuarioResponse> creado = authService.registrar(request);

        if (creado.isEmpty()) {
            return respuesta(HttpStatus.CONFLICT, "EMAIL_YA_REGISTRADO",
                    "Ya existe una cuenta con ese correo");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(creado.get());
    }

    @Operation(summary = "Iniciar sesión",
               description = "Autentica con email y contraseña. Retorna el token JWT y el rol. La cuenta debe tener el email verificado.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getContrasena()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);

            String role = null;
            if (userDetails instanceof Usuario u) {
                role = u.getRol().name();
            }

            return ResponseEntity.ok(Map.of("token", token, "role", role != null ? role : ""));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("codigo", "CREDENCIALES_INVALIDAS",
                            "mensaje", "Correo o contraseña incorrectos, o cuenta no verificada"));
        }
    }

    @Operation(summary = "Verificar email",
               description = "Activa la cuenta usando el token recibido por correo.")
    @PostMapping("/verificar-email")
    public ResponseEntity<Map<String, String>> verificarEmail(@RequestParam String token) {
        ResultadoVerificacion resultado = authService.verificarEmail(token);

        return switch (resultado) {
            case EXITOSA -> respuesta(HttpStatus.OK, "VERIFICADO",
                    "Correo verificado. Ya puedes iniciar sesión");
            case TOKEN_INVALIDO -> respuesta(HttpStatus.BAD_REQUEST, "TOKEN_INVALIDO",
                    "El enlace de verificación no es válido");
            case TOKEN_USADO -> respuesta(HttpStatus.BAD_REQUEST, "TOKEN_USADO",
                    "Este enlace ya fue utilizado");
            case TOKEN_EXPIRADO -> respuesta(HttpStatus.BAD_REQUEST, "TOKEN_EXPIRADO",
                    "El enlace expiró. Solicita uno nuevo");
        };
    }

    @Operation(summary = "Reenviar enlace de verificación",
               description = "Envía un nuevo enlace al correo si la cuenta existe y aún no está verificada.")
    @PostMapping("/reenviar-verificacion")
    public ResponseEntity<Map<String, String>> reenviarVerificacion(
            @Valid @RequestBody ReenviarVerificacionRequest request) {
        authService.reenviarVerificacion(request.getEmail());
        return respuesta(HttpStatus.OK, "SOLICITUD_RECIBIDA",
                "Si el correo está registrado y sin verificar, te enviamos un nuevo enlace");
    }

    private ResponseEntity<Map<String, String>> respuesta(HttpStatus status, String codigo, String mensaje) {
        return ResponseEntity.status(status).body(Map.of("codigo", codigo, "mensaje", mensaje));
    }
}
