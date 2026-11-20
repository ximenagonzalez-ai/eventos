package co.edu.unbosque.eventos.model;

/**
 * Resultado del intento de login.
 * Encapsula el estado, el token JWT (si fue exitoso) y el rol del usuario.
 */
public class ResultadoLogin {

    private final EstadoLogin estado;
    private final String token;
    private final String rol;
    private final Long usuarioId;

    private ResultadoLogin(EstadoLogin estado, String token, String rol, Long usuarioId) {
        this.estado = estado;
        this.token = token;
        this.rol = rol;
        this.usuarioId = usuarioId;
    }
 
    public static ResultadoLogin exitoso(String token, String rol, Long usuarioId) {
        return new ResultadoLogin(EstadoLogin.EXITOSO, token, rol, usuarioId);
    }
 
    public static ResultadoLogin fallido(EstadoLogin estado) {
        return new ResultadoLogin(estado, null, null, null);
    }
 
    public EstadoLogin getEstado() {
        return estado;
    }
 
    public String getToken() {
        return token;
    }
 
    public String getRol() {
        return rol;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }
}