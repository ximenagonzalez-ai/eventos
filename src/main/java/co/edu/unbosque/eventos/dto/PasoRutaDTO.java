package co.edu.unbosque.eventos.dto;

/** Una instruccion de la ruta, por ejemplo "Gira a la derecha en Calle 100". */
public class PasoRutaDTO {

    private String instruccion;
    private String calle;
    private double distanciaMetros;
    private double duracionSegundos;

    public PasoRutaDTO() {
    }

    public PasoRutaDTO(String instruccion, String calle, double distanciaMetros, double duracionSegundos) {
        this.instruccion = instruccion;
        this.calle = calle;
        this.distanciaMetros = distanciaMetros;
        this.duracionSegundos = duracionSegundos;
    }

    public String getInstruccion() {
        return instruccion;
    }

    public void setInstruccion(String instruccion) {
        this.instruccion = instruccion;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
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
}
