package co.edu.unbosque.eventos.controller;

import co.edu.unbosque.eventos.DTO.CategoriaDTO;
import co.edu.unbosque.eventos.service.CategoriaService;
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
@RequestMapping("/categoria")
@RequiredArgsConstructor
@Tag(name = "Categorías", description = "Gestión de categorías de eventos")
@SecurityRequirement(name = "bearerAuth")
public class CategoriaController {

    private final CategoriaService categoriaService;

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
        Optional<CategoriaDTO> dto = categoriaService.getById(id);
        return dto.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Contar categorías")
    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(categoriaService.count());
    }

    @Operation(summary = "Crear categoría (ADMIN)")
    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoriaDTO> crear(@RequestBody CategoriaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.crear(dto));
    }

    @Operation(summary = "Actualizar categoría (ADMIN)")
    @PutMapping(path = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoriaDTO> actualizar(@PathVariable Long id,
                                                    @RequestBody CategoriaDTO dto) {
        Optional<CategoriaDTO> updated = categoriaService.actualizar(id, dto);
        return updated.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar categoría (ADMIN)")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        boolean ok = categoriaService.eliminar(id);
        if (!ok) return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Categoría eliminada");
    }
}
