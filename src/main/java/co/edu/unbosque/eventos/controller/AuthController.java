package co.edu.unbosque.eventos.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.edu.unbosque.eventos.dto.LoginRequestDTO;
import co.edu.unbosque.eventos.dto.OlvidePasswordRequestDTO;
import co.edu.unbosque.eventos.dto.ReenviarVerificacionRequestDTO;
import co.edu.unbosque.eventos.dto.RegistroRequestDTO;
import co.edu.unbosque.eventos.dto.RestablecimientoPasswordRequestDTO;
import co.edu.unbosque.eventos.dto.UsuarioResponseDTO;
import co.edu.unbosque.eventos.model.ResultadoLogin;
import co.edu.unbosque.eventos.model.ResultadoRestablecimiento;
import co.edu.unbosque.eventos.model.ResultadoVerificacion;
import co.edu.unbosque.eventos.service.AuthService;
 
import java.util.Map;
import java.util.Optional;
 
@RestController
@RequestMapping("/api/auth")
public class AuthController {
 
    private final AuthService authService;
 
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
 
    @Operation(summary = "Registrar usuario",
            description = "Crea la cuenta y envía por correo el enlace de verificación.")
    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegistroRequestDTO request) {
        Optional<UsuarioResponseDTO> creado = authService.registrar(request);
 
        if (creado.isEmpty()) {
            return respuesta(HttpStatus.CONFLICT, "EMAIL_YA_REGISTRADO",
                    "Ya existe una cuenta con ese correo");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(creado.get());
    }
 
    @Operation(summary = "Iniciar sesión",
            description = "Autentica con email y contraseña. Retorna el token JWT y el rol. La cuenta debe tener el email verificado.")
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequestDTO request) {
        ResultadoLogin resultado = authService.login(request);
 
        return switch (resultado.getEstado()) {
            case EXITOSO -> ResponseEntity.ok(Map.of(
                    "token", resultado.getToken(),
                    "role", resultado.getRol()));
            case CREDENCIALES_INVALIDAS -> respuesta(HttpStatus.UNAUTHORIZED, "CREDENCIALES_INVALIDAS",
                    "Correo o contraseña incorrectos");
            case CUENTA_NO_VERIFICADA -> respuesta(HttpStatus.FORBIDDEN, "CUENTA_NO_VERIFICADA",
                    "Debes verificar tu correo antes de iniciar sesión");
            case CUENTA_BLOQUEADA -> respuesta(HttpStatus.FORBIDDEN, "CUENTA_BLOQUEADA",
                    "Tu cuenta está bloqueada. Contacta al administrador");
        };
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
 
    @Operation(summary = "Reenviar verificación",
            description = "Envía un nuevo enlace de verificación si el correo está registrado y sin verificar.")
    @PostMapping("/reenviar-verificacion")
    public ResponseEntity<Map<String, String>> reenviarVerificacion(
            @Valid @RequestBody ReenviarVerificacionRequestDTO request) {
        authService.reenviarVerificacion(request.getEmail());
        return respuesta(HttpStatus.OK, "SOLICITUD_RECIBIDA",
                "Si el correo está registrado y sin verificar, te enviamos un nuevo enlace");
    }
 
    @Operation(summary = "Olvidé mi contraseña",
            description = "Envía por correo un enlace para restablecer la contraseña. Siempre responde igual, exista o no el correo.")
    @PostMapping("/olvide-password")
    public ResponseEntity<Map<String, String>> olvidePassword(
            @Valid @RequestBody OlvidePasswordRequestDTO request) {
        authService.solicitarRestablecimiento(request.getEmail());
        return respuesta(HttpStatus.OK, "SOLICITUD_RECIBIDA",
                "Si el correo está registrado, te enviamos un enlace para restablecer tu contraseña");
    }
 
    @Operation(summary = "Restablecer contraseña",
            description = "Cambia la contraseña usando el token recibido por correo.")
    @PostMapping("/restablecer-password")
    public ResponseEntity<Map<String, String>> restablecerPassword(
            @Valid @RequestBody RestablecimientoPasswordRequestDTO request) {
        ResultadoRestablecimiento resultado = authService
                .restablecerPassword(request.getToken(), request.getNuevaContrasena());
 
        return switch (resultado) {
            case EXITOSO -> respuesta(HttpStatus.OK, "PASSWORD_RESTABLECIDA",
                    "Contraseña actualizada. Ya puedes iniciar sesión");
            case TOKEN_INVALIDO -> respuesta(HttpStatus.BAD_REQUEST, "TOKEN_INVALIDO",
                    "El enlace de restablecimiento no es válido");
            case TOKEN_USADO -> respuesta(HttpStatus.BAD_REQUEST, "TOKEN_USADO",
                    "Este enlace ya fue utilizado");
            case TOKEN_EXPIRADO -> respuesta(HttpStatus.BAD_REQUEST, "TOKEN_EXPIRADO",
                    "El enlace expiró. Solicita uno nuevo");
        };
    }
 
    private ResponseEntity<Map<String, String>> respuesta(HttpStatus status, String codigo, String mensaje) {
        return ResponseEntity.status(status).body(Map.of("codigo", codigo, "mensaje", mensaje));
    }
}