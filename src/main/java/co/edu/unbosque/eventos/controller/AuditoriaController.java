package co.edu.unbosque.eventos.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.eventos.model.Auditoria;
import co.edu.unbosque.eventos.service.AuditoriaService;

@RestController
@RequestMapping("/api/auditorias")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Auditoria> listarPorUsuario(@PathVariable Long usuarioId) {
        return auditoriaService.listarPorUsuario(usuarioId);
    }
}