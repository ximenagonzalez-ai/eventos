package co.edu.unbosque.eventos.dto;

/** Lo que envía el frontend: coordenadas de origen (usuario) y destino (evento). */
public class RutaRequestDTO {

    private double origenLat;
    private double origenLng;
    private double destinoLat;
    private double destinoLng;

    public RutaRequestDTO() {}

    public double getOrigenLat() { return origenLat; }
    public void setOrigenLat(double origenLat) { this.origenLat = origenLat; }

    public double getOrigenLng() { return origenLng; }
    public void setOrigenLng(double origenLng) { this.origenLng = origenLng; }

    public double getDestinoLat() { return destinoLat; }
    public void setDestinoLat(double destinoLat) { this.destinoLat = destinoLat; }

    public double getDestinoLng() { return destinoLng; }
    public void setDestinoLng(double destinoLng) { this.destinoLng = destinoLng; }
}
