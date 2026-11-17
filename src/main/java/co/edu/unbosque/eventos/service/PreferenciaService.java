package co.edu.unbosque.eventos.service;

import co.edu.unbosque.eventos.dto.PreferenciaRequestDTO;
import co.edu.unbosque.eventos.dto.PreferenciaResponseDTO;
import co.edu.unbosque.eventos.model.PreferenciaUsuario;
import co.edu.unbosque.eventos.model.Usuario;
import co.edu.unbosque.eventos.repository.PreferenciaUsuarioRepository;
import co.edu.unbosque.eventos.repository.UsuarioRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Preferencias de diseno del usuario. Sin HTTP: devuelve Optional / boolean como "senal"
 * y el controller decide el codigo de respuesta.
 */
@Service
public class PreferenciaService {

    private final PreferenciaUsuarioRepository preferenciaRepository;
    private final UsuarioRepository usuarioRepository;

    public PreferenciaService(PreferenciaUsuarioRepository preferenciaRepository,
                              UsuarioRepository usuarioRepository) {
        this.preferenciaRepository = preferenciaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Devuelve las preferencias guardadas, o las de por defecto si todavia no ha guardado ninguna.
     * Optional vacio = el usuario no existe.
     */
    public Optional<PreferenciaResponseDTO> obtener(String email) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        if (usuario.isEmpty()) {
            return Optional.empty();
        }
        PreferenciaUsuario p = preferenciaRepository.findByUsuarioId(usuario.get().getId())
                .orElseGet(PreferenciaUsuario::new); // valores por defecto, sin guardar
        return Optional.of(PreferenciaResponseDTO.convertirAPreferenciaResponse(p));
    }

    /** Crea o actualiza las preferencias. Optional vacio = el usuario no existe. */
    public Optional<PreferenciaResponseDTO> guardar(String email, PreferenciaRequestDTO request) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        if (usuario.isEmpty()) {
            return Optional.empty();
        }
        PreferenciaUsuario p = preferenciaRepository.findByUsuarioId(usuario.get().getId())
                .orElseGet(PreferenciaUsuario::new);
        p.setUsuario(usuario.get());
        p.setTema(request.getTema());
        p.setColorPrimario(request.getColorPrimario().toUpperCase());
        p.setColorSecundario(request.getColorSecundario().toUpperCase());
        p.setTamanoFuente(request.getTamanoFuente());
        return Optional.of(PreferenciaResponseDTO.convertirAPreferenciaResponse(preferenciaRepository.save(p)));
    }

    /** Borra las preferencias guardadas (vuelve al diseno por defecto). false = el usuario no existe. */
    public boolean restablecer(String email) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        if (usuario.isEmpty()) {
            return false;
        }
        preferenciaRepository.findByUsuarioId(usuario.get().getId()).ifPresent(preferenciaRepository::delete);
        return true;
    }
}
