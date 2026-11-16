package co.edu.unbosque.eventos.controller;

import co.edu.unbosque.eventos.dto.CategoriaDTO;
import co.edu.unbosque.eventos.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categoria")
@Tag(name = "Categorías", description = "Gestión de categorías de eventos")
@SecurityRequirement(name = "bearerAuth")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @Operation(summary = "Obtener todas las categorías")
    @GetMapping("/getall")
    public ResponseEntity<List<CategoriaDTO>> getAll() {
        List<CategoriaDTO> lista = categoriaService.getAll();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Obtener categoría por ID")
    @GetMapping("/getbyid/{id}")
    public ResponseEntity<CategoriaDTO> getById(@PathVariable Long id) {
        CategoriaDTO dto = categoriaService.getById(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Contar categorías")
    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(categoriaService.count());
    }

    @Operation(summary = "Crear categoría (ADMIN)")
    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> crear(@RequestBody CategoriaDTO dto) {
        int status = categoriaService.create(dto);
        if (status == 0) return ResponseEntity.status(HttpStatus.CREATED).body("Categoría creada");
        return ResponseEntity.status(HttpStatus.CONFLICT).body("El nombre ya está en uso");
    }

    @Operation(summary = "Actualizar categoría (ADMIN)")
    @PutMapping(path = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody CategoriaDTO dto) {
        int status = categoriaService.updateById(id, dto);
        return switch (status) {
            case 0 -> ResponseEntity.ok("Categoría actualizada");
            case 1 -> ResponseEntity.status(HttpStatus.CONFLICT).body("El nombre ya está en uso");
            default -> ResponseEntity.notFound().build();
        };
    }

    @Operation(summary = "Eliminar categoría (ADMIN)")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        int status = categoriaService.deleteById(id);
        if (status != 0) return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Categoría eliminada");
    }
}
