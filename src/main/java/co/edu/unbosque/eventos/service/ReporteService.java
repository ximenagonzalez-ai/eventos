package co.edu.unbosque.eventos.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.eventos.DTO.ReporteDTO;
import co.edu.unbosque.eventos.repository.BoletaRepository;

@Service
public class ReporteService {

    private final BoletaRepository boletaRepository;

    public ReporteService(BoletaRepository boletaRepository) {
        this.boletaRepository = boletaRepository;
    }

    @Transactional(readOnly = true)
    public List<ReporteDTO> ventasPorEvento() {
        List<ReporteDTO> lista = new ArrayList<>();
        for (Object[] fila : boletaRepository.ventasPorEvento()) {
            ReporteDTO dto = new ReporteDTO();
            dto.setEvento((String) fila[0]);
            dto.setBoletasVendidas((Long) fila[1]);
            dto.setTotalVendido((BigDecimal) fila[2]);
            lista.add(dto);
        }
        return lista;
    }
}