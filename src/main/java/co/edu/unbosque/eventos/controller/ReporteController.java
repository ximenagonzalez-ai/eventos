package co.edu.unbosque.eventos.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.eventos.dto.ReporteDTO;
import co.edu.unbosque.eventos.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/reportes")
@Tag(name = "Reportes", description = "Reportes administrativos de ventas y estadísticas (solo ADMIN)")
@SecurityRequirement(name = "bearerAuth")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @Operation(summary = "Ventas por evento",
               description = "Retorna el total de boletas vendidas e ingresos generados por cada evento.")
    @GetMapping("/ventas-por-evento")
    public List<ReporteDTO> ventasPorEvento() {
        return reporteService.ventasPorEvento();
    }
}
package co.edu.unbosque.eventos.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.eventos.dto.ReporteDTO;
import co.edu.unbosque.eventos.service.ReporteService;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/ventas-por-evento")
    public List<ReporteDTO> ventasPorEvento() {
        return reporteService.ventasPorEvento();
    }
}
