package co.edu.unbosque.eventos.dto;

import co.edu.unbosque.eventos.model.Rol;

/** DTO para crear y actualizar usuarios desde el panel de admin. */
public class UsuarioDTO {

    private Long id;
    private String nombre;
    private String email;
    /** Solo se usa para crear o cambiar contraseña. Nunca se devuelve en respuestas. */
    private String contrasena;
    private Rol rol;
    private boolean activo;
    private boolean emailVerificado;

    public UsuarioDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public boolean isEmailVerificado() { return emailVerificado; }
    public void setEmailVerificado(boolean emailVerificado) { this.emailVerificado = emailVerificado; }
}
