package co.edu.unbosque.eventos.controller;

import co.edu.unbosque.eventos.dto.EventoDTO;
import co.edu.unbosque.eventos.model.Evento.EstadoEvento;
import co.edu.unbosque.eventos.service.EventoApiClient;
import co.edu.unbosque.eventos.service.EventoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/evento")
@RequiredArgsConstructor
@Tag(name = "Eventos", description = "Catálogo de eventos: teatro, ferias, conciertos y más")
@SecurityRequirement(name = "bearerAuth")
public class EventoController {

    private final EventoService eventoService;
    private final EventoApiClient eventoApiClient;

    // ---- CRUD (ADMIN) ----

    @Operation(summary = "Crear evento (ADMIN)")
    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> crear(@RequestBody EventoDTO dto) {
        int status = eventoService.crear(dto);
        return switch (status) {
            case 0 -> ResponseEntity.status(HttpStatus.CREATED).body("Evento creado exitosamente");
            case 1 -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Categoría no encontrada");
            case 2 -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lugar no encontrado");
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el evento");
        };
    }

    @Operation(summary = "Actualizar evento (ADMIN)")
    @PutMapping(path = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody EventoDTO dto) {
        int status = eventoService.actualizar(id, dto);
        return switch (status) {
            case 0 -> ResponseEntity.ok("Evento actualizado exitosamente");
            case 1 -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Categoría no encontrada");
            case 2 -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lugar no encontrado");
            default -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Evento no encontrado");
        };
    }

    @Operation(summary = "Eliminar evento (ADMIN)")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        boolean ok = eventoService.eliminar(id);
        if (!ok) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Evento no encontrado");
        return ResponseEntity.ok("Evento eliminado exitosamente");
    }

    // ---- Consultas (USER y ADMIN) ----

    @Operation(summary = "Obtener todos los eventos")
    @GetMapping("/getall")
    public ResponseEntity<List<EventoDTO>> getAll() {
        List<EventoDTO> lista = eventoService.getAll();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Obtener evento por ID")
    @GetMapping("/getbyid/{id}")
    public ResponseEntity<EventoDTO> getById(@PathVariable Long id) {
        Optional<EventoDTO> dto = eventoService.getById(id);
        return dto.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Filtrar por categoría")
    @GetMapping("/bycategoria/{categoriaId}")
    public ResponseEntity<List<EventoDTO>> getByCategoria(@PathVariable Long categoriaId) {
        List<EventoDTO> lista = eventoService.getByCategoria(categoriaId);
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Filtrar por estado (ACTIVO, PROXIMO, FINALIZADO, CANCELADO)")
    @GetMapping("/byestado")
    public ResponseEntity<List<EventoDTO>> getByEstado(@RequestParam EstadoEvento estado) {
        List<EventoDTO> lista = eventoService.getByEstado(estado);
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Filtrar por ciudad")
    @GetMapping("/bycity")
    public ResponseEntity<List<EventoDTO>> getByCiudad(@RequestParam String ciudad) {
        List<EventoDTO> lista = eventoService.getByCiudad(ciudad);
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Buscar eventos por nombre")
    @GetMapping("/search")
    public ResponseEntity<List<EventoDTO>> search(@RequestParam String nombre) {
        List<EventoDTO> lista = eventoService.buscarPorNombre(nombre);
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Filtrar por rango de fechas (yyyy-MM-ddTHH:mm:ss)")
    @GetMapping("/byfecha")
    public ResponseEntity<List<EventoDTO>> getByFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        List<EventoDTO> lista = eventoService.getByRangoFechas(inicio, fin);
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Contar eventos")
    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(eventoService.count());
    }

    // ---- Sincronización Ticketmaster (ADMIN) ----

    @Operation(summary = "Sincronizar desde Ticketmaster (ADMIN)")
    @PostMapping("/sync")
    public ResponseEntity<String> sincronizar() {
        int nuevos = eventoApiClient.sincronizar();
        return ResponseEntity.ok("Sincronización completada. Eventos nuevos: " + nuevos);
    }
}
