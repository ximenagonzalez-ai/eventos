package co.edu.unbosque.eventos.service;

import co.edu.unbosque.eventos.DTO.UsuarioDTO;
import co.edu.unbosque.eventos.model.Rol;
import co.edu.unbosque.eventos.model.Usuario;
import co.edu.unbosque.eventos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public long count() {
        return usuarioRepository.count();
    }

    public boolean exist(Long id) {
        return usuarioRepository.existsById(id);
    }

    public List<UsuarioDTO> getAll() {
        return usuarioRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<UsuarioDTO> getById(Long id) {
        return usuarioRepository.findById(id).map(this::toDTO);
    }

    /**
     * Crea un usuario desde el panel de admin (no requiere verificación de email).
     * Retorna 0=ok, 1=email ya en uso.
     */
    @Transactional
    public int crear(UsuarioDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail().trim().toLowerCase())) return 1;

        Usuario u = Usuario.builder()
                .nombre(dto.getNombre().trim())
                .email(dto.getEmail().trim().toLowerCase())
                .contrasenaHash(passwordEncoder.encode(dto.getContrasena()))
                .rol(dto.getRol() != null ? dto.getRol() : Rol.USER)
                .activo(true)
                .emailVerificado(true)   // creado por admin, ya verificado
                .build();

        usuarioRepository.save(u);
        return 0;
    }

    /**
     * Actualiza nombre, email, rol y activo. Si contrasena no es null la cambia.
     * Retorna 0=ok, 1=email ya en uso por otro usuario, 2=no existe.
     */
    @Transactional
    public int actualizar(Long id, UsuarioDTO dto) {
        Optional<Usuario> found = usuarioRepository.findById(id);
        if (found.isEmpty()) return 2;

        Usuario u = found.get();

        String nuevoEmail = dto.getEmail().trim().toLowerCase();
        if (!nuevoEmail.equals(u.getEmail())
                && usuarioRepository.existsByEmail(nuevoEmail)) return 1;

        u.setNombre(dto.getNombre().trim());
        u.setEmail(nuevoEmail);
        u.setRol(dto.getRol() != null ? dto.getRol() : u.getRol());
        u.setActivo(dto.isActivo());

        if (dto.getContrasena() != null && !dto.getContrasena().isBlank()) {
            u.setContrasenaHash(passwordEncoder.encode(dto.getContrasena()));
        }

        usuarioRepository.save(u);
        return 0;
    }

    @Transactional
    public boolean eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) return false;
        usuarioRepository.deleteById(id);
        return true;
    }

    @Transactional
    public boolean eliminarPorEmail(String email) {
        Optional<Usuario> u = usuarioRepository.findByEmail(email.trim().toLowerCase());
        if (u.isEmpty()) return false;
        usuarioRepository.delete(u.get());
        return true;
    }

    /** Convierte entidad → DTO (sin exponer el hash de contraseña) */
    private UsuarioDTO toDTO(Usuario u) {
        return UsuarioDTO.builder()
                .id(u.getId())
                .nombre(u.getNombre())
                .email(u.getEmail())
                .rol(u.getRol())
                .activo(u.isActivo())
                .emailVerificado(u.isEmailVerificado())
                .build();
    }
}
