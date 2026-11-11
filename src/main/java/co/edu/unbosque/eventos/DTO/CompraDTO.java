package co.edu.unbosque.eventos.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import co.edu.unbosque.eventos.model.Compra;
import co.edu.unbosque.eventos.model.EstadoCompra;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompraDTO {

    // Datos que llegan al comprar la boleta
    private Long usuarioId;
    private Long tipoBoletaId;
    private Integer cantidad;

    // Datos que se devuelven 
    private Long id;
    private BigDecimal total;
    private EstadoCompra estado;
    private LocalDateTime fechaCompra;

    public static CompraDTO convertirACompraDTO(Compra compra) {
        CompraDTO dto = new CompraDTO();
        dto.setId(compra.getId());
        dto.setUsuarioId(compra.getUsuario().getId());
        dto.setTotal(compra.getTotal());
        dto.setEstado(compra.getEstado());
        dto.setFechaCompra(compra.getFechaCompra());
        return dto;
    }
}