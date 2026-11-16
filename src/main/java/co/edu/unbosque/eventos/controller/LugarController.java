package co.edu.unbosque.eventos.controller;

import co.edu.unbosque.eventos.dto.LugarDTO;
import co.edu.unbosque.eventos.service.LugarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/lugar")
@RequiredArgsConstructor
@Tag(name = "Lugares", description = "Gestión de lugares de eventos (con coordenadas para mapa)")
@SecurityRequirement(name = "bearerAuth")
public class LugarController {

    private final LugarService lugarService;

    @Operation(summary = "Obtener todos los lugares")
    @GetMapping("/getall")
    public ResponseEntity<List<LugarDTO>> getAll() {
        List<LugarDTO> lista = lugarService.getAll();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Obtener lugar por ID")
    @GetMapping("/getbyid/{id}")
    public ResponseEntity<LugarDTO> getById(@PathVariable Long id) {
        Optional<LugarDTO> dto = lugarService.getById(id);
        return dto.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Filtrar lugares por ciudad")
    @GetMapping("/bycity")
    public ResponseEntity<List<LugarDTO>> getByCiudad(@RequestParam String ciudad) {
        List<LugarDTO> lista = lugarService.getByCiudad(ciudad);
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Contar lugares")
    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(lugarService.count());
    }

    @Operation(summary = "Crear lugar (ADMIN)")
    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LugarDTO> crear(@RequestBody LugarDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lugarService.crear(dto));
    }

    @Operation(summary = "Actualizar lugar (ADMIN)")
    @PutMapping(path = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LugarDTO> actualizar(@PathVariable Long id,
                                                @RequestBody LugarDTO dto) {
        Optional<LugarDTO> updated = lugarService.actualizar(id, dto);
        return updated.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar lugar (ADMIN)")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        boolean ok = lugarService.eliminar(id);
        if (!ok) return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Lugar eliminado");
    }
}
