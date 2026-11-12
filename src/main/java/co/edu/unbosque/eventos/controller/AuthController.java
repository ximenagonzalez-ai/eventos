package co.edu.unbosque.eventos.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
import co.edu.unbosque.eventos.DTO.RegistroRequest;
import co.edu.unbosque.eventos.DTO.ReenviarVerificacionRequest;
import co.edu.unbosque.eventos.DTO.UsuarioResponse;
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
 
    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegistroRequest request) {
        Optional<UsuarioResponse> creado = authService.registrar(request);
 
        if (creado.isEmpty()) {
            return respuesta(HttpStatus.CONFLICT, "EMAIL_YA_REGISTRADO",
                    "Ya existe una cuenta con ese correo");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(creado.get());
    }
 
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
 
    @PostMapping("/reenviar-verificacion")
    public ResponseEntity<Map<String, String>> reenviarVerificacion(
            @Valid @RequestBody ReenviarVerificacionRequest request) {
        authService.reenviarVerificacion(request.getEmail());
        return respuesta(HttpStatus.OK, "SOLICITUD_RECIBIDA",
                "Si el correo está registrado y sin verificar, te enviamos un nuevo enlace");
    }
 
    // TODO (siguiente paso): POST /login -> debe rechazar si !emailVerificado o !activo
 
    private ResponseEntity<Map<String, String>> respuesta(HttpStatus status, String codigo, String mensaje) {
        return ResponseEntity.status(status).body(Map.of("codigo", codigo, "mensaje", mensaje));
    }
}