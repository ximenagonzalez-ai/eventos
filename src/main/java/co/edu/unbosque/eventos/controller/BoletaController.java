package co.edu.unbosque.eventos.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.eventos.dto.BoletaDTO;
import co.edu.unbosque.eventos.dto.CompraDTO;
import co.edu.unbosque.eventos.service.BoletaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/boletas")
@Tag(name = "Boletas", description = "Compra y consulta de boletas para eventos")
@SecurityRequirement(name = "bearerAuth")
public class BoletaController {

    private final BoletaService boletaService;

    public BoletaController(BoletaService boletaService) {
        this.boletaService = boletaService;
    }

    @Operation(summary = "Comprar boletas", description = "Compra una cantidad de boletas de un tipo específico para un usuario.")
    @PostMapping("/comprar")
    public List<BoletaDTO> comprar(@RequestBody CompraDTO dto) {
        return boletaService.comprar(dto.getUsuarioId(), dto.getTipoBoletaId(), dto.getCantidad());
    }

    @Operation(summary = "Ver boletas de una compra")
    @GetMapping("/compra/{compraId}")
    public List<BoletaDTO> listarPorCompra(@PathVariable Long compraId) {
        return boletaService.listarPorCompra(compraId);
    }
}