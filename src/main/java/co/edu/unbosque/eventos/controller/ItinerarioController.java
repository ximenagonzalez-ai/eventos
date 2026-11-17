package co.edu.unbosque.eventos.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.eventos.dto.ItinerarioDTO;
import co.edu.unbosque.eventos.service.ItinerarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/itinerarios")
@Tag(name = "Itinerarios", description = "Gestión de itinerarios personales de eventos por usuario")
@SecurityRequirement(name = "bearerAuth")
public class ItinerarioController {

    private final ItinerarioService itinerarioService;

    public ItinerarioController(ItinerarioService itinerarioService) {
        this.itinerarioService = itinerarioService;
    }

    @Operation(summary = "Crear itinerario")
    @PostMapping
    public ItinerarioDTO crear(@RequestBody ItinerarioDTO dto) {
        return itinerarioService.crear(dto.getUsuarioId(), dto.getNombre());
    }

    @Operation(summary = "Agregar evento al itinerario")
    @PostMapping("/{itinerarioId}/eventos/{eventoId}")
    public ItinerarioDTO agregarEvento(@PathVariable Long itinerarioId, @PathVariable Long eventoId) {
        return itinerarioService.agregarEvento(itinerarioId, eventoId);
    }

    @Operation(summary = "Obtener itinerario por ID")
    @GetMapping("/{itinerarioId}")
    public ItinerarioDTO obtener(@PathVariable Long itinerarioId) {
        return itinerarioService.obtener(itinerarioId);
    }

    @Operation(summary = "Listar itinerarios de un usuario")
    @GetMapping("/usuario/{usuarioId}")
    public List<ItinerarioDTO> listarPorUsuario(@PathVariable Long usuarioId) {
        return itinerarioService.listarPorUsuario(usuarioId);
    }
}