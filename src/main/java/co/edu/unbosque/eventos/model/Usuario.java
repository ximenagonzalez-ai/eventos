package co.edu.unbosque.eventos.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Usuario del sistema. Implementa UserDetails para que Spring Security
 * pueda usarlo directamente para autenticar peticiones.
 *
 * Flujo de registro:
 *   1. POST /api/auth/registro → se crea con emailVerificado = false
 *   2. El usuario hace clic en el enlace del correo
 *   3. emailVerificado pasa a true → ya puede hacer login
 *
 * Usuarios creados por admin (POST /user/create) se crean ya verificados.
 */
@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre completo del usuario. */
    @Column(nullable = false, length = 100)
    private String nombre;

    /** Email único — se usa como identificador de login. */
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    /** Contraseña encriptada con BCrypt. Nunca se expone en respuestas. */
    @Column(name = "contrasena_hash", nullable = false)
    private String contrasenaHash;

    /** Rol del usuario: USER o ADMIN. Por defecto USER. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol = Rol.USER;

    /** Si false, el usuario no puede hacer login. */
    @Column(nullable = false)
    private boolean activo = true;

    /**
     * El usuario debe confirmar su correo antes de poder hacer login.
     * Mientras sea false, isEnabled() retorna false y Spring Security bloquea el acceso.
     */
    @Column(name = "email_verificado", nullable = false)
    private boolean emailVerificado = false;

    /** Se asigna automáticamente al hacer el primer INSERT. */
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    public Usuario() {}

    @PrePersist
    void antesDeGuardar() {
        this.fechaCreacion = LocalDateTime.now();
    }

    // ---- Métodos de UserDetails (Spring Security los llama internamente) ----

    /** Retorna el rol con prefijo "ROLE_" que exige Spring Security. */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    /** Spring Security llama a este método para comparar con la contraseña ingresada. */
    @Override
    public String getPassword() {
        return contrasenaHash;
    }

    /** Usamos el email como username de login. */
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    /** La cuenta se bloquea si el admin pone activo = false. */
    @Override
    public boolean isAccountNonLocked() { return activo; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    /** Solo puede loguear si está activo Y confirmó su email. */
    @Override
    public boolean isEnabled() { return activo && emailVerificado; }

    // ---- Getters y Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContrasenaHash() { return contrasenaHash; }
    public void setContrasenaHash(String contrasenaHash) { this.contrasenaHash = contrasenaHash; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public boolean isEmailVerificado() { return emailVerificado; }
    public void setEmailVerificado(boolean emailVerificado) { this.emailVerificado = emailVerificado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
