package co.edu.unbosque.eventos.dto;

import java.util.List;

/** Lo que devuelve el backend al frontend: instrucciones, distancia y tiempo. */
public class RutaResponseDTO {

    private List<String> instrucciones;
    private String distanciaTotal;
    private String duracionTotal;

    public RutaResponseDTO() {}

    public RutaResponseDTO(List<String> instrucciones, String distanciaTotal, String duracionTotal) {
        this.instrucciones = instrucciones;
        this.distanciaTotal = distanciaTotal;
        this.duracionTotal = duracionTotal;
    }

    public List<String> getInstrucciones() { return instrucciones; }
    public void setInstrucciones(List<String> instrucciones) { this.instrucciones = instrucciones; }

    public String getDistanciaTotal() { return distanciaTotal; }
    public void setDistanciaTotal(String distanciaTotal) { this.distanciaTotal = distanciaTotal; }

    public String getDuracionTotal() { return duracionTotal; }
    public void setDuracionTotal(String duracionTotal) { this.duracionTotal = duracionTotal; }
}
