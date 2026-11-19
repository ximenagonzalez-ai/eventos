package co.edu.unbosque.eventos.dto;

import java.util.List;

/**
 * Ruta entre el origen (ubicacion del usuario) y el destino (lugar del evento).
 * "trazado" es una lista de puntos [latitud, longitud] lista para dibujar con Leaflet (L.polyline).
 */
public class RutaDTO {

    private String modo;
    private double distanciaMetros;
    private double duracionSegundos;
    private double origenLat;
    private double origenLng;
    private double destinoLat;
    private double destinoLng;
    private List<double[]> trazado;
    private List<PasoRutaDTO> pasos;
    private String enlaceGoogleMaps;

    public RutaDTO() {
    }

    public String getModo() {
        return modo;
    }

    public void setModo(String modo) {
        this.modo = modo;
    }

    public double getDistanciaMetros() {
        return distanciaMetros;
    }

    public void setDistanciaMetros(double distanciaMetros) {
        this.distanciaMetros = distanciaMetros;
    }

    public double getDuracionSegundos() {
        return duracionSegundos;
    }

    public void setDuracionSegundos(double duracionSegundos) {
        this.duracionSegundos = duracionSegundos;
    }

    public double getOrigenLat() {
        return origenLat;
    }

    public void setOrigenLat(double origenLat) {
        this.origenLat = origenLat;
    }

    public double getOrigenLng() {
        return origenLng;
    }

    public void setOrigenLng(double origenLng) {
        this.origenLng = origenLng;
    }

    public double getDestinoLat() {
        return destinoLat;
    }

    public void setDestinoLat(double destinoLat) {
        this.destinoLat = destinoLat;
    }

    public double getDestinoLng() {
        return destinoLng;
    }

    public void setDestinoLng(double destinoLng) {
        this.destinoLng = destinoLng;
    }

    public List<double[]> getTrazado() {
        return trazado;
    }

    public void setTrazado(List<double[]> trazado) {
        this.trazado = trazado;
    }

    public List<PasoRutaDTO> getPasos() {
        return pasos;
    }

    public void setPasos(List<PasoRutaDTO> pasos) {
        this.pasos = pasos;
    }

    public String getEnlaceGoogleMaps() {
        return enlaceGoogleMaps;
    }

    public void setEnlaceGoogleMaps(String enlaceGoogleMaps) {
        this.enlaceGoogleMaps = enlaceGoogleMaps;
    }
}
