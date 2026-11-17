package co.edu.unbosque.eventos.controller;

import co.edu.unbosque.eventos.dto.PreferenciaRequestDTO;
import co.edu.unbosque.eventos.dto.PreferenciaResponseDTO;
import co.edu.unbosque.eventos.service.PreferenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Requiere estar logueado (JWT). El usuario sale del token, no se manda en la URL. */
@RestController
@RequestMapping("/api/preferencias")
@Tag(name = "Preferencias", description = "Personalizacion del diseno (tema, colores, tamano de letra)")
public class PreferenciaController {

    private final PreferenciaService preferenciaService;

    public PreferenciaController(PreferenciaService preferenciaService) {
        this.preferenciaService = preferenciaService;
    }

    @Operation(summary = "Ver mis preferencias",
            description = "Devuelve las preferencias guardadas o las de por defecto si aun no ha guardado ninguna.")
    @GetMapping
    public ResponseEntity<?> obtener(Authentication authentication) {
        Optional<PreferenciaResponseDTO> r = preferenciaService.obtener(authentication.getName());
        if (r.isEmpty()) {
            return usuarioNoEncontrado();
        }
        return ResponseEntity.ok(r.get());
    }

    @Operation(summary = "Guardar mis preferencias",
            description = "Crea o actualiza tema (CLARO/OSCURO), colores hexadecimales y tamano de fuente (PEQUENO/MEDIANO/GRANDE).")
    @PutMapping
    public ResponseEntity<?> guardar(Authentication authentication,
                                     @Valid @RequestBody PreferenciaRequestDTO request) {
        Optional<PreferenciaResponseDTO> r = preferenciaService.guardar(authentication.getName(), request);
        if (r.isEmpty()) {
            return usuarioNoEncontrado();
        }
        return ResponseEntity.ok(r.get());
    }

    @Operation(summary = "Restablecer diseno por defecto")
    @DeleteMapping
    public ResponseEntity<?> restablecer(Authentication authentication) {
        if (!preferenciaService.restablecer(authentication.getName())) {
            return usuarioNoEncontrado();
        }
        return ResponseEntity.ok(Map.of("codigo", "PREFERENCIAS_RESTABLECIDAS",
                "mensaje", "Se restablecio el diseno por defecto"));
    }

    private ResponseEntity<Map<String, String>> usuarioNoEncontrado() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("codigo", "USUARIO_NO_ENCONTRADO", "mensaje", "El usuario no existe"));
    }
}