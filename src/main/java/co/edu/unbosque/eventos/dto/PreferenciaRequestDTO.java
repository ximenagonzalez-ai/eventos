package co.edu.unbosque.eventos.dto;

import co.edu.unbosque.eventos.model.TamanoFuente;
import co.edu.unbosque.eventos.model.Tema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class PreferenciaRequestDTO {

    @NotNull(message = "El tema es obligatorio")
    private Tema tema;

    @NotNull(message = "El color primario es obligatorio")
    @Pattern(regexp = "^#[0-9a-fA-F]{6}$", message = "El color primario debe ser hexadecimal, ej: #1976D2")
    private String colorPrimario;

    @NotNull(message = "El color secundario es obligatorio")
    @Pattern(regexp = "^#[0-9a-fA-F]{6}$", message = "El color secundario debe ser hexadecimal, ej: #FF9800")
    private String colorSecundario;

    @NotNull(message = "El tamano de fuente es obligatorio")
    private TamanoFuente tamanoFuente;

    public PreferenciaRequestDTO() {
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
