package co.edu.unbosque.eventos.DTO;

import co.edu.unbosque.eventos.model.Rol;
import co.edu.unbosque.eventos.model.Usuario;
 
import java.time.LocalDateTime;
 
public class UsuarioResponse {
 
    private Long id;
    private String nombre;
    private String email;
    private Rol rol;
    private boolean activo;
    private LocalDateTime fechaCreacion;
 
    public UsuarioResponse() {
    }
 
    public UsuarioResponse(Long id, String nombre, String email, Rol rol,
                           boolean activo, LocalDateTime fechaCreacion) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }
 
    public static UsuarioResponse convertirAusuarioResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.isActivo(),
                usuario.getFechaCreacion()
        );
    }
 
    public Long getId() {
        return id;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
 
    public String getNombre() {
        return nombre;
    }
 
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
 
    public String getEmail() {
        return email;
    }
 
    public void setEmail(String email) {
        this.email = email;
    }
 
    public Rol getRol() {
        return rol;
    }
 
    public void setRol(Rol rol) {
        this.rol = rol;
    }
 
    public boolean isActivo() {
        return activo;
    }
 
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
 
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
 
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}