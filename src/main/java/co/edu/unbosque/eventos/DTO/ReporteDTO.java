package co.edu.unbosque.eventos.DTO;

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