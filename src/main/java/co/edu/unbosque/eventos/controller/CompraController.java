package co.edu.unbosque.eventos.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.eventos.DTO.CompraDTO;
import co.edu.unbosque.eventos.model.Compra;
import co.edu.unbosque.eventos.service.CompraService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
public class CompraController {

    private final CompraService compraService;

    @GetMapping("/usuario/{usuarioId}")
    public List<CompraDTO> listarPorUsuario(@PathVariable Long usuarioId) {
        List<CompraDTO> respuesta = new ArrayList<>();
        for (Compra compra : compraService.listarPorUsuario(usuarioId)) {
            respuesta.add(CompraDTO.convertirACompraDTO(compra));
        }
        return respuesta;
    }
}