package co.edu.unbosque.eventos.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import co.edu.unbosque.eventos.model.Boleta;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoletaDTO {

    private Long id;
    private Long compraId;
    private String codigo;
    private String tipoBoleta;
    private String evento;
    private BigDecimal precioPagado;
    private LocalDateTime fechaEmision;

    public static BoletaDTO convertirABoletaDTO(Boleta boleta) {
        return new BoletaDTO(
                boleta.getId(),
                boleta.getCompra().getId(),
                boleta.getCodigo(),
                boleta.getTipoBoleta().getNombre(),
                boleta.getTipoBoleta().getEvento().getNombre(),
                boleta.getPrecioPagado(),
                boleta.getFechaEmision()
        );
    }
}