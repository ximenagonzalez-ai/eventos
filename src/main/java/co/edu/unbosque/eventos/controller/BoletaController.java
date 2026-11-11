package co.edu.unbosque.eventos.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.eventos.DTO.BoletaDTO;
import co.edu.unbosque.eventos.DTO.CompraDTO;
import co.edu.unbosque.eventos.service.BoletaService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/boletas")
@RequiredArgsConstructor
public class BoletaController {

    private final BoletaService boletaService;

    @PostMapping("/comprar")
    public List<BoletaDTO> comprar(@RequestBody CompraDTO dto) {
        return boletaService.comprar(dto.getUsuarioId(), dto.getTipoBoletaId(), dto.getCantidad());
    }

    @GetMapping("/compra/{compraId}")
    public List<BoletaDTO> listarPorCompra(@PathVariable Long compraId) {
        return boletaService.listarPorCompra(compraId);
    }
}