package co.edu.unbosque.eventos.controller;

import co.edu.unbosque.eventos.dto.UsuarioDTO;
import co.edu.unbosque.eventos.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Transactional
@Tag(name = "Gestión de Usuarios", description = "Endpoints para administrar usuarios del sistema")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService usuarioService;

    // ---- Lectura (USER y ADMIN) ----

    @Operation(summary = "Obtener todos los usuarios",
               description = "Retorna la lista completa de usuarios. Accesible para USER y ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de usuarios"),
        @ApiResponse(responseCode = "204", description = "No hay usuarios registrados")
    })
    @GetMapping("/getall")
    public ResponseEntity<List<UsuarioDTO>> getAll() {
        List<UsuarioDTO> lista = usuarioService.getAll();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Obtener usuario por ID",
               description = "Retorna el usuario con el ID indicado. Accesible para USER y ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/getbyid/{id}")
    public ResponseEntity<UsuarioDTO> getById(@PathVariable Long id) {
        Optional<UsuarioDTO> dto = usuarioService.getById(id);
        return dto.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Verificar si existe un usuario",
               description = "Retorna true si el ID existe. Accesible para USER y ADMIN.")
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> exists(@PathVariable Long id) {
        boolean found = usuarioService.exist(id);
        return found ? ResponseEntity.ok(true)
                     : ResponseEntity.status(HttpStatus.NO_CONTENT).body(false);
    }

    @Operation(summary = "Contar usuarios",
               description = "Retorna el total de usuarios registrados. Accesible para USER y ADMIN.")
    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(usuarioService.count());
    }

    // ---- Escritura (solo ADMIN) ----

    @Operation(summary = "Crear usuario (ADMIN)",
               description = "Crea un nuevo usuario desde el panel de administración. El usuario queda verificado y activo de inmediato.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
            content = @Content(examples = @ExampleObject(value = "Usuario creado exitosamente"))),
        @ApiResponse(responseCode = "406", description = "El email ya está en uso",
            content = @Content(examples = @ExampleObject(value = "El email ya está en uso")))
    })
    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> crear(@RequestBody UsuarioDTO dto) {
        if (dto.getEmail() == null || dto.getEmail().contains("<") || dto.getEmail().contains(">")) {
            return ResponseEntity.badRequest().body("Solicitud con caracteres inválidos");
        }
        int status = usuarioService.crear(dto);
        if (status == 0) return ResponseEntity.status(HttpStatus.CREATED).body("Usuario creado exitosamente");
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("El email ya está en uso");
    }

    @Operation(summary = "Actualizar usuario (ADMIN)",
               description = "Actualiza nombre, email, rol, activo y opcionalmente la contraseña.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
        @ApiResponse(responseCode = "226", description = "El nuevo email ya está en uso"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping(path = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        int status = usuarioService.actualizar(id, dto);
        return switch (status) {
            case 0 -> ResponseEntity.ok("Usuario actualizado exitosamente");
            case 1 -> ResponseEntity.status(HttpStatus.IM_USED).body("El nuevo email ya está en uso");
            default -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        };
    }

    @Operation(summary = "Eliminar usuario por ID (ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/deletebyid/{id}")
    public ResponseEntity<String> eliminarPorId(@PathVariable Long id) {
        boolean ok = usuarioService.eliminar(id);
        if (!ok) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        return ResponseEntity.ok("Usuario eliminado exitosamente");
    }

    @Operation(summary = "Eliminar usuario por email (ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/deletebyemail")
    public ResponseEntity<String> eliminarPorEmail(@RequestParam String email) {
        boolean ok = usuarioService.eliminarPorEmail(email);
        if (!ok) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        return ResponseEntity.ok("Usuario eliminado exitosamente");
    }
}
