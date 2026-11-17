package co.edu.unbosque.eventos.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.eventos.dto.CompraDTO;
import co.edu.unbosque.eventos.model.Compra;
import co.edu.unbosque.eventos.service.CompraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/compras")
@Tag(name = "Compras", description = "Gestión de compras de boletas por usuario")
@SecurityRequirement(name = "bearerAuth")
public class CompraController {

    private final CompraService compraService;

    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    @Operation(summary = "Ver compras de un usuario")
    @GetMapping("/usuario/{usuarioId}")
    public List<CompraDTO> listarPorUsuario(@PathVariable Long usuarioId) {
        List<CompraDTO> respuesta = new ArrayList<>();
        for (Compra compra : compraService.listarPorUsuario(usuarioId)) {
            respuesta.add(CompraDTO.convertirACompraDTO(compra));
        }
        return respuesta;
    }
}