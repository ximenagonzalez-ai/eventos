package co.edu.unbosque.eventos.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.eventos.DTO.ItinerarioDTO;
import co.edu.unbosque.eventos.service.ItinerarioService;

@RestController
@RequestMapping("/api/itinerarios")
public class ItinerarioController {

    private final ItinerarioService itinerarioService;

    public ItinerarioController(ItinerarioService itinerarioService) {
        this.itinerarioService = itinerarioService;
    }

    @PostMapping
    public ItinerarioDTO crear(@RequestBody ItinerarioDTO dto) {
        return itinerarioService.crear(dto.getUsuarioId(), dto.getNombre());
    }

    @PostMapping("/{itinerarioId}/eventos/{eventoId}")
    public ItinerarioDTO agregarEvento(@PathVariable Long itinerarioId, @PathVariable Long eventoId) {
        return itinerarioService.agregarEvento(itinerarioId, eventoId);
    }

    @GetMapping("/{itinerarioId}")
    public ItinerarioDTO obtener(@PathVariable Long itinerarioId) {
        return itinerarioService.obtener(itinerarioId);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<ItinerarioDTO> listarPorUsuario(@PathVariable Long usuarioId) {
        return itinerarioService.listarPorUsuario(usuarioId);
    }
}