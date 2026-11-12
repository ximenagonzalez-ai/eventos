package co.edu.unbosque.eventos.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;
 
import co.edu.unbosque.eventos.DTO.RegistroRequest;
import co.edu.unbosque.eventos.DTO.UsuarioResponse;
import co.edu.unbosque.eventos.model.ResultadoVerificacion;
import co.edu.unbosque.eventos.model.TipoCorreo;
import co.edu.unbosque.eventos.model.TipoToken;
import co.edu.unbosque.eventos.model.TokenUsuario;
import co.edu.unbosque.eventos.model.Usuario;
import co.edu.unbosque.eventos.repository.TokenUsuarioRepository;
import co.edu.unbosque.eventos.repository.UsuarioRepository;
 
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
 
@Service
public class AuthService {
 
    private final UsuarioRepository usuarioRepository;
    private final TokenUsuarioRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final CorreoService correoService;
 
    @Value("${app.frontend-url}")
    private String frontendUrl;
 
    @Value("${app.token.verificacion-horas:24}")
    private long horasVerificacion;
 
    public AuthService(UsuarioRepository usuarioRepository,
                       TokenUsuarioRepository tokenRepository,
                       PasswordEncoder passwordEncoder,
                       CorreoService correoService) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.correoService = correoService;
    }
 
    /**
     * Crea la cuenta (sin verificar) y envía el correo con el enlace de verificación.
     *
     * @return el usuario creado, o Optional.empty() si el correo ya estaba registrado
     */
    @Transactional
    public Optional<UsuarioResponse> registrar(RegistroRequest request) {
        String email = request.getEmail().trim().toLowerCase();
 
        if (usuarioRepository.existsByEmail(email)) {
            return Optional.empty();
        }
 
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre().trim());
        usuario.setEmail(email);
        usuario.setContrasenaHash(passwordEncoder.encode(request.getContrasena()));
        usuario = usuarioRepository.save(usuario);
 
        enviarCorreoVerificacion(usuario);
        return Optional.of(UsuarioResponse.convertirAusuarioResponse(usuario));
    }
 
    /** Se llama cuando el usuario hace clic en el enlace del correo. */
    @Transactional
    public ResultadoVerificacion verificarEmail(String valorToken) {
        Optional<TokenUsuario> encontrado = tokenRepository.findByToken(valorToken)
                .filter(t -> t.getTipo() == TipoToken.VERIFICACION_EMAIL);
 
        if (encontrado.isEmpty()) {
            return ResultadoVerificacion.TOKEN_INVALIDO;
        }
 
        TokenUsuario token = encontrado.get();
 
        if (token.isUsado()) {
            return ResultadoVerificacion.TOKEN_USADO;
        }
        if (token.estaExpirado()) {
            return ResultadoVerificacion.TOKEN_EXPIRADO;
        }
 
        Usuario usuario = token.getUsuario();
        usuario.setEmailVerificado(true);
        token.setUsado(true);
        usuarioRepository.save(usuario);
        tokenRepository.save(token);
 
        String cuerpo = "<h2>¡Tu cuenta está verificada, "
                + HtmlUtils.htmlEscape(usuario.getNombre()) + "!</h2>"
                + "<p>Ya puedes iniciar sesión:</p>"
                + "<p><a href=\"" + frontendUrl + "/login\">Iniciar sesión</a></p>";
        correoService.enviarCorreo(usuario, TipoCorreo.BIENVENIDA, "¡Bienvenido!", cuerpo);
 
        return ResultadoVerificacion.EXITOSA;
    }
 
    /**
     * Reenvía el enlace de verificación. No revela si el correo existe:
     * si no existe o ya está verificado, simplemente no hace nada.
     */
    @Transactional
    public void reenviarVerificacion(String emailCrudo) {
        usuarioRepository.findByEmail(emailCrudo.trim().toLowerCase())
                .filter(u -> !u.isEmailVerificado())
                .ifPresent(usuario -> {
                    List<TokenUsuario> anteriores = tokenRepository
                            .findByUsuarioAndTipoAndUsadoFalse(usuario, TipoToken.VERIFICACION_EMAIL);
                    anteriores.forEach(t -> t.setUsado(true));
                    tokenRepository.saveAll(anteriores);
 
                    enviarCorreoVerificacion(usuario);
                });
    }
 
    private void enviarCorreoVerificacion(Usuario usuario) {
        TokenUsuario token = new TokenUsuario();
        token.setToken(UUID.randomUUID().toString());
        token.setUsuario(usuario);
        token.setTipo(TipoToken.VERIFICACION_EMAIL);
        token.setFechaExpiracion(LocalDateTime.now().plusHours(horasVerificacion));
        tokenRepository.save(token);
 
        String enlace = frontendUrl + "/verificar-email?token=" + token.getToken();
        String cuerpo = "<h2>Hola, " + HtmlUtils.htmlEscape(usuario.getNombre()) + "</h2>"
                + "<p>Gracias por registrarte. Confirma tu correo para activar tu cuenta:</p>"
                + "<p><a href=\"" + enlace + "\">Verificar mi correo</a></p>"
                + "<p>El enlace vence en " + horasVerificacion + " horas.</p>";
 
        correoService.enviarCorreo(usuario, TipoCorreo.VERIFICACION_EMAIL,
                "Confirma tu correo", cuerpo);
    }
}