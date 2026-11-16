package co.edu.unbosque.eventos.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import co.edu.unbosque.eventos.security.JwtUtil;
import co.edu.unbosque.eventos.dto.LoginRequestDTO;
import co.edu.unbosque.eventos.dto.RegistroRequestDTO;
import co.edu.unbosque.eventos.dto.UsuarioResponseDTO;
import co.edu.unbosque.eventos.model.EstadoLogin;
import co.edu.unbosque.eventos.model.ResultadoLogin;
import co.edu.unbosque.eventos.model.ResultadoRestablecimiento;
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
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
 
    @Value("${app.frontend-url}")
    private String frontendUrl;
 
    @Value("${app.token.verificacion-horas:24}")
    private long horasVerificacion;
 
    @Value("${app.token.restablecimiento-minutos:30}")
    private long minutosRestablecimiento;
 
    public AuthService(UsuarioRepository usuarioRepository,
                       TokenUsuarioRepository tokenRepository,
                       PasswordEncoder passwordEncoder,
                       CorreoService correoService,
                       AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.correoService = correoService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }
 
    // ===================== REGISTRO =====================
 
    /**
     * Crea la cuenta (sin verificar) y envía el correo con el enlace de verificación.
     *
     * @return el usuario creado, o Optional.empty() si el correo ya estaba registrado
     */
    @Transactional
    public Optional<UsuarioResponseDTO> registrar(RegistroRequestDTO request) {
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
        return Optional.of(UsuarioResponseDTO.convertirAusuarioResponse(usuario));
    }
 
    // ===================== LOGIN =====================
 
    /**
     * Inicia sesión. El orden importa:
     * 1) se valida correo y contraseña (si fallan, siempre CREDENCIALES_INVALIDAS);
     * 2) solo si son correctos, se revisa que el correo esté verificado y la cuenta activa.
     * Así nadie puede averiguar el estado de una cuenta sin conocer su contraseña.
     */
    public ResultadoLogin login(LoginRequestDTO request) {
        String email = request.getEmail().trim().toLowerCase();
 
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getContrasena()));
        } catch (AuthenticationException e) {
            return ResultadoLogin.fallido(EstadoLogin.CREDENCIALES_INVALIDAS);
        }
 
        Optional<Usuario> encontrado = usuarioRepository.findByEmail(email);
        if (encontrado.isEmpty()) {
            return ResultadoLogin.fallido(EstadoLogin.CREDENCIALES_INVALIDAS);
        }
        Usuario usuario = encontrado.get();
 
        if (!usuario.isEmailVerificado()) {
            return ResultadoLogin.fallido(EstadoLogin.CUENTA_NO_VERIFICADA);
        }
        if (!usuario.isActivo()) {
            return ResultadoLogin.fallido(EstadoLogin.CUENTA_BLOQUEADA);
        }
 
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);
        return ResultadoLogin.exitoso(token, usuario.getRol().name());
    }
 
    // ===================== VERIFICACIÓN DE CORREO =====================
 
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
                    invalidarTokensPendientes(usuario, TipoToken.VERIFICACION_EMAIL);
                    enviarCorreoVerificacion(usuario);
                });
    }
 
    // ===================== RESTABLECER CONTRASEÑA =====================
 
    /**
     * Paso 1: el usuario dice que olvidó su contraseña. No revela si el correo existe:
     * si no existe o la cuenta está bloqueada, simplemente no hace nada.
     */
    @Transactional
    public void solicitarRestablecimiento(String emailCrudo) {
        usuarioRepository.findByEmail(emailCrudo.trim().toLowerCase())
                .filter(Usuario::isActivo)
                .ifPresent(usuario -> {
                    invalidarTokensPendientes(usuario, TipoToken.RESTABLECER_PASSWORD);
 
                    TokenUsuario token = crearTokenTemporal(usuario, TipoToken.RESTABLECER_PASSWORD,
                            LocalDateTime.now().plusMinutes(minutosRestablecimiento));
 
                    String enlace = frontendUrl + "/restablecer-password?token=" + token.getToken();
                    String cuerpo = "<h2>Hola, " + HtmlUtils.htmlEscape(usuario.getNombre()) + "</h2>"
                            + "<p>Recibimos una solicitud para restablecer tu contraseña.</p>"
                            + "<p><a href=\"" + enlace + "\">Crear una contraseña nueva</a></p>"
                            + "<p>El enlace vence en " + minutosRestablecimiento + " minutos y solo puede usarse una vez.</p>"
                            + "<p>Si no fuiste tú, ignora este correo: tu contraseña no cambiará.</p>";
 
                    correoService.enviarCorreo(usuario, TipoCorreo.RESTABLECER_PASSWORD,
                            "Restablece tu contraseña", cuerpo);
                });
    }
 
    /** Paso 2: el usuario llega desde el enlace del correo y escribe su contraseña nueva. */
    @Transactional
    public ResultadoRestablecimiento restablecerPassword(String valorToken, String nuevaContrasena) {
        Optional<TokenUsuario> encontrado = tokenRepository.findByToken(valorToken)
                .filter(t -> t.getTipo() == TipoToken.RESTABLECER_PASSWORD);
 
        if (encontrado.isEmpty()) {
            return ResultadoRestablecimiento.TOKEN_INVALIDO;
        }
 
        TokenUsuario token = encontrado.get();
 
        if (token.isUsado()) {
            return ResultadoRestablecimiento.TOKEN_USADO;
        }
        if (token.estaExpirado()) {
            return ResultadoRestablecimiento.TOKEN_EXPIRADO;
        }
 
        Usuario usuario = token.getUsuario();
        usuario.setContrasenaHash(passwordEncoder.encode(nuevaContrasena));
        usuarioRepository.save(usuario);
 
        token.setUsado(true);
        tokenRepository.save(token);
        invalidarTokensPendientes(usuario, TipoToken.RESTABLECER_PASSWORD);
 
        String cuerpo = "<h2>Hola, " + HtmlUtils.htmlEscape(usuario.getNombre()) + "</h2>"
                + "<p>Tu contraseña fue modificada correctamente.</p>"
                + "<p>Si no fuiste tú, restablécela de nuevo desde "
                + "<a href=\"" + frontendUrl + "/login\">la página de inicio de sesión</a>.</p>";
        correoService.enviarCorreo(usuario, TipoCorreo.PASSWORD_CAMBIADA,
                "Tu contraseña fue cambiada", cuerpo);
 
        return ResultadoRestablecimiento.EXITOSO;
    }
 
    // ===================== AUXILIARES =====================
 
    private void enviarCorreoVerificacion(Usuario usuario) {
        TokenUsuario token = crearTokenTemporal(usuario, TipoToken.VERIFICACION_EMAIL,
                LocalDateTime.now().plusHours(horasVerificacion));
 
        String enlace = frontendUrl + "/verificar-email?token=" + token.getToken();
        String cuerpo = "<h2>Hola, " + HtmlUtils.htmlEscape(usuario.getNombre()) + "</h2>"
                + "<p>Gracias por registrarte. Confirma tu correo para activar tu cuenta:</p>"
                + "<p><a href=\"" + enlace + "\">Verificar mi correo</a></p>"
                + "<p>El enlace vence en " + horasVerificacion + " horas.</p>";
 
        correoService.enviarCorreo(usuario, TipoCorreo.VERIFICACION_EMAIL,
                "Confirma tu correo", cuerpo);
    }
 
    private TokenUsuario crearTokenTemporal(Usuario usuario, TipoToken tipo, LocalDateTime expiracion) {
        TokenUsuario token = new TokenUsuario();
        token.setToken(UUID.randomUUID().toString());
        token.setUsuario(usuario);
        token.setTipo(tipo);
        token.setFechaExpiracion(expiracion);
        return tokenRepository.save(token);
    }
 
    private void invalidarTokensPendientes(Usuario usuario, TipoToken tipo) {
        List<TokenUsuario> pendientes = tokenRepository
                .findByUsuarioAndTipoAndUsadoFalse(usuario, tipo);
        pendientes.forEach(t -> t.setUsado(true));
        tokenRepository.saveAll(pendientes);
    }
}