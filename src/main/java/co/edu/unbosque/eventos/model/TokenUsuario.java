package co.edu.unbosque.eventos.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
 
@Entity
@Table(name = "tokens_usuario")
public class TokenUsuario {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(nullable = false, unique = true, length = 100)
    private String token;
 
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoToken tipo;
 
    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime fechaExpiracion;
 
    @Column(nullable = false)
    private boolean usado = false;
 
    public TokenUsuario() {
    }
 
    public boolean estaExpirado() {
        return LocalDateTime.now().isAfter(fechaExpiracion);
    }
 
    public Long getId() {
        return id;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
 
    public String getToken() {
        return token;
    }
 
    public void setToken(String token) {
        this.token = token;
    }
 
    public Usuario getUsuario() {
        return usuario;
    }
 
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
 
    public TipoToken getTipo() {
        return tipo;
    }
 
    public void setTipo(TipoToken tipo) {
        this.tipo = tipo;
    }
 
    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }
 
    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }
 
    public boolean isUsado() {
        return usado;
    }
 
    public void setUsado(boolean usado) {
        this.usado = usado;
    }
}