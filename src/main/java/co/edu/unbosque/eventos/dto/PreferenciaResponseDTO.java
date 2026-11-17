package co.edu.unbosque.eventos.dto;

import co.edu.unbosque.eventos.model.PreferenciaUsuario;
import co.edu.unbosque.eventos.model.TamanoFuente;
import co.edu.unbosque.eventos.model.Tema;

public class PreferenciaResponseDTO {

    private Tema tema;
    private String colorPrimario;
    private String colorSecundario;
    private TamanoFuente tamanoFuente;

    public PreferenciaResponseDTO() {
    }

    public static PreferenciaResponseDTO convertirAPreferenciaResponse(PreferenciaUsuario p) {
        PreferenciaResponseDTO dto = new PreferenciaResponseDTO();
        dto.setTema(p.getTema());
        dto.setColorPrimario(p.getColorPrimario());
        dto.setColorSecundario(p.getColorSecundario());
        dto.setTamanoFuente(p.getTamanoFuente());
        return dto;
    }

    public Tema getTema() {
        return tema;
    }

    public void setTema(Tema tema) {
        this.tema = tema;
    }

    public String getColorPrimario() {
        return colorPrimario;
    }

    public void setColorPrimario(String colorPrimario) {
        this.colorPrimario = colorPrimario;
    }

    public String getColorSecundario() {
        return colorSecundario;
    }

    public void setColorSecundario(String colorSecundario) {
        this.colorSecundario = colorSecundario;
    }

    public TamanoFuente getTamanoFuente() {
        return tamanoFuente;
    }

    public void setTamanoFuente(TamanoFuente tamanoFuente) {
        this.tamanoFuente = tamanoFuente;
    }
}