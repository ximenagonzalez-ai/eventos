package co.edu.unbosque.eventos.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.eventos.model.Auditoria;
import co.edu.unbosque.eventos.service.AuditoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auditoria")
@RequiredArgsConstructor
@Tag(name = "Auditoría", description = "Consulta del historial de acciones realizadas por los usuarios (solo ADMIN)")
@SecurityRequirement(name = "bearerAuth")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    @Operation(summary = "Ver acciones de un usuario",
               description = "Retorna el historial completo de acciones realizadas por el usuario con el ID indicado.")
    @GetMapping("/usuario/{usuarioId}")
    public List<Auditoria> listarPorUsuario(@PathVariable Long usuarioId) {
        return auditoriaService.listarPorUsuario(usuarioId);
    }
}
