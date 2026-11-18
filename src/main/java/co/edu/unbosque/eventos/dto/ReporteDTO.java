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
