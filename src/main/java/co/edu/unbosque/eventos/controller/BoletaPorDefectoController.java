package co.edu.unbosque.eventos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.eventos.service.BoletaPorDefectoService;

/**
 * Ruta bajo /evento/**, que SecurityConfig ya limita al rol ADMIN.
 */
@RestController
@RequestMapping("/evento")
@Tag(name = "Eventos", description = "Catálogo de eventos")
@SecurityRequirement(name = "bearerAuth")
public class BoletaPorDefectoController {

    private final BoletaPorDefectoService boletaPorDefectoService;

    public BoletaPorDefectoController(BoletaPorDefectoService boletaPorDefectoService) {
        this.boletaPorDefectoService = boletaPorDefectoService;
    }

    @Operation(summary = "Crear boletas General y VIP en eventos sin boletas (ADMIN)")
    @PostMapping("/generar-boletas")
    public ResponseEntity<String> generarBoletas() {
        int n = boletaPorDefectoService.generarFaltantes();
        return ResponseEntity.ok("Eventos con boletas nuevas: " + n);
    }
}
