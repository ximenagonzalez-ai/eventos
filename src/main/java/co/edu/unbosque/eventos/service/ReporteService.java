package co.edu.unbosque.eventos.service;

import co.edu.unbosque.eventos.dto.ReporteDTO;
import co.edu.unbosque.eventos.model.Boleta;
import co.edu.unbosque.eventos.repository.BoletaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio de reportes administrativos.
 * Genera resúmenes de ventas agrupados por evento.
 * Solo accessible para ADMIN desde GET /api/reportes/ventas-por-evento
 */
@Service
public class ReporteService {

    private final BoletaRepository boletaRepository;

    public ReporteService(BoletaRepository boletaRepository) {
        this.boletaRepository = boletaRepository;
    }

    /**
     * Calcula el total de boletas vendidas e ingresos generados por cada evento.
     * Agrupa todas las boletas de la BD por nombre de evento y suma sus precios.
     *
     * @return Lista de ReporteDTO con nombre del evento, total boletas e ingresos
     */
    public List<ReporteDTO> ventasPorEvento() {

        List<Boleta> todasLasBoletas = boletaRepository.findAll();

        // Agrupamos por nombre del evento usando un mapa
        Map<String, ReporteDTO> mapa = new LinkedHashMap<>();

        for (Boleta boleta : todasLasBoletas) {
            String nombreEvento = boleta.getTipoBoleta().getEvento().getNombre();

            if (!mapa.containsKey(nombreEvento)) {
                mapa.put(nombreEvento, new ReporteDTO(nombreEvento, 0L, 0.0));
            }

            ReporteDTO reporte = mapa.get(nombreEvento);
            reporte.setTotalBoletas(reporte.getTotalBoletas() + 1);
            reporte.setTotalIngresos(
                reporte.getTotalIngresos() + boleta.getPrecioPagado().doubleValue()
            );
        }

        return new ArrayList<>(mapa.values());
    }
}
