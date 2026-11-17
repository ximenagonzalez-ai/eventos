package co.edu.unbosque.eventos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/** Preferencias de diseno de un usuario. Una fila por usuario. */
@Entity
@Table(name = "preferencias_usuario")
public class PreferenciaUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tema tema = Tema.CLARO;

    /** Color hexadecimal, ej: #1976D2 */
    @Column(nullable = false, length = 7)
    private String colorPrimario = "#1976D2";

    @Column(nullable = false, length = 7)
    private String colorSecundario = "#FF9800";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TamanoFuente tamanoFuente = TamanoFuente.MEDIANO;

    public PreferenciaUsuario() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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
