package co.edu.unbosque.eventos.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Usuario registrado en nuestro sistema de eventos.
 *
 * Implementa UserDetails de Spring Security, lo que significa que esta misma
 * clase es la que Spring usa para autenticar y autorizar. No hace falta una
 * clase separada de seguridad.
 *
 * Flujo de registro:
 *   1. El usuario llama a POST /api/auth/registro
 *   2. Se crea con emailVerificado = false
 *   3. Le llega un correo con un enlace de verificación (token UUID)
 *   4. Al hacer clic en el enlace, emailVerificado pasa a true
 *   5. Ahora puede hacer login con POST /api/auth/login
 *
 * Los usuarios creados directamente por un admin (via POST /user/create)
 * se crean con emailVerificado = true para que puedan loguear de inmediato.
 *
 * Roles:
 *   - USER: puede ver el catálogo de eventos
 *   - ADMIN: puede crear/editar eventos, usuarios, categorías, lugares y ver auditoría
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario implements UserDetails {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre completo del usuario, ej: "Juan Pérez". */
    @Column(nullable = false, length = 100)
    private String nombre;

    /** Email único — es el "username" que usamos para hacer login. */
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    /** Contraseña encriptada con BCrypt. Nunca se devuelve en las respuestas. */
    @Column(name = "contrasena_hash", nullable = false)
    private String contrasenaHash;

    /** Rol del usuario: USER o ADMIN. Por defecto USER. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Rol rol = Rol.USER;

    /** Si false, el usuario no puede hacer login aunque tenga el email verificado. */
    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;

    /** Se asigna automáticamente al guardar por primera vez (ver @PrePersist). */
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Indica si el usuario confirmó su email haciendo clic en el enlace que le enviamos.
     * Mientras sea false, el login falla aunque la contraseña sea correcta.
     */
    @Column(name = "email_verificado", nullable = false)
    @Builder.Default
    private boolean emailVerificado = false;

    /** JPA llama a este método antes de hacer el INSERT. Asigna la fecha de creación. */
    @PrePersist
    void antesDeGuardar() {
        this.fechaCreacion = LocalDateTime.now();
    }

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
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public boolean isEmailVerificado() { return emailVerificado; }
    public void setEmailVerificado(boolean emailVerificado) { this.emailVerificado = emailVerificado; }

    // ---- Métodos de UserDetails (Spring Security los llama internamente) ----

    /** Retorna el rol con prefijo "ROLE_" que exige Spring Security, ej: "ROLE_ADMIN". */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    /** Spring Security necesita este método; nosotros guardamos el hash en contrasenaHash. */
    @Override
    public String getPassword() { return contrasenaHash; }

    /** Usamos el email como identificador de login en lugar de un username inventado. */
    @Override
    public String getUsername() { return email; }

    @Override public boolean isAccountNonExpired() { return true; }

    /** La cuenta se bloquea si el admin pone activo = false. */
    @Override public boolean isAccountNonLocked() { return activo; }

    @Override public boolean isCredentialsNonExpired() { return true; }

    /** El usuario solo puede loguear si está activo Y verificó su email. */
    @Override public boolean isEnabled() { return activo && emailVerificado; }
}
