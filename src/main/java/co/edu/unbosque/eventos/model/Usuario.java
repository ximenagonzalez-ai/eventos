package co.edu.unbosque.eventos.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
 
@Entity
@Table(name = "usuarios")
public class Usuario {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(nullable = false, length = 100)
    private String nombre;
 
    @Column(nullable = false, unique = true, length = 150)
    private String email;
 
    @Column(name = "contrasena_hash", nullable = false)
    private String contrasenaHash;
 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol = Rol.USER;
 
    @Column(nullable = false)
    private boolean activo = true;
 
    @Column(name = "email_verificado", nullable = false)
    private boolean emailVerificado = false;
 
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
 
    public Usuario() {
    }
 
    @PrePersist
    void antesDeGuardar() {
        this.fechaCreacion = LocalDateTime.now();
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
 
    public String getContrasenaHash() {
        return contrasenaHash;
    }
 
    public void setContrasenaHash(String contrasenaHash) {
        this.contrasenaHash = contrasenaHash;
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
 
    public boolean isEmailVerificado() {
        return emailVerificado;
    }
 
    public void setEmailVerificado(boolean emailVerificado) {
        this.emailVerificado = emailVerificado;
    }
 
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
}