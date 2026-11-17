package co.edu.unbosque.eventos.controller;

import co.edu.unbosque.eventos.dto.LugarDTO;
import co.edu.unbosque.eventos.service.LugarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lugar")
@Tag(name = "Lugares", description = "Gestión de lugares de eventos (con coordenadas para mapa)")
@SecurityRequirement(name = "bearerAuth")
public class LugarController {

    private final LugarService lugarService;

    public LugarController(LugarService lugarService) {
        this.lugarService = lugarService;
    }

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
        LugarDTO dto = lugarService.getById(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
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
    public ResponseEntity<String> crear(@RequestBody LugarDTO dto) {
        int status = lugarService.create(dto);
        if (status == 0) return ResponseEntity.status(HttpStatus.CREATED).body("Lugar creado");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear lugar");
    }

    @Operation(summary = "Actualizar lugar (ADMIN)")
    @PutMapping(path = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody LugarDTO dto) {
        int status = lugarService.updateById(id, dto);
        if (status == 0) return ResponseEntity.ok("Lugar actualizado");
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar lugar (ADMIN)")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        int status = lugarService.deleteById(id);
        if (status != 0) return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Lugar eliminado");
    }
}
