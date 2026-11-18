package co.edu.unbosque.eventos.dto;
/** DTO con el resumen de ventas de boletas por evento. */
public class ReporteDTO {

    private String nombreEvento;
    private Long totalBoletas;
    private Double totalIngresos;

    public ReporteDTO() {}

    public ReporteDTO(String nombreEvento, Long totalBoletas, Double totalIngresos) {
        this.nombreEvento = nombreEvento;
        this.totalBoletas = totalBoletas;
        this.totalIngresos = totalIngresos;
    }

    public String getNombreEvento() { return nombreEvento; }
    public void setNombreEvento(String nombreEvento) { this.nombreEvento = nombreEvento; }

    public Long getTotalBoletas() { return totalBoletas; }
    public void setTotalBoletas(Long totalBoletas) { this.totalBoletas = totalBoletas; }

    public Double getTotalIngresos() { return totalIngresos; }
    public void setTotalIngresos(Double totalIngresos) { this.totalIngresos = totalIngresos; }
}
import java.math.BigDecimal;

public class ReporteDTO {

    private String evento;
    private Long boletasVendidas;
    private BigDecimal totalVendido;

    public ReporteDTO() {}

    public String getEvento() { return evento; }
    public void setEvento(String evento) { this.evento = evento; }

    public Long getBoletasVendidas() { return boletasVendidas; }
    public void setBoletasVendidas(Long boletasVendidas) { this.boletasVendidas = boletasVendidas; }

    public BigDecimal getTotalVendido() { return totalVendido; }
    public void setTotalVendido(BigDecimal totalVendido) { this.totalVendido = totalVendido; }
}
