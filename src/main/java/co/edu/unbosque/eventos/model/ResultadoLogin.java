package co.edu.unbosque.eventos.model;

/**
 * Resultado del intento de login.
 * Encapsula el estado, el token JWT (si fue exitoso) y el rol del usuario.
 */
public class ResultadoLogin {

    private final EstadoLogin estado;
    private final String token;
    private final String rol;

    private ResultadoLogin(EstadoLogin estado, String token, String rol) {
        this.estado = estado;
        this.token = token;
        this.rol = rol;
    }

    /** Crea un resultado exitoso con el token y el rol del usuario. */
    public static ResultadoLogin exitoso(String token, String rol) {
        return new ResultadoLogin(EstadoLogin.EXITOSO, token, rol);
    }

    /** Crea un resultado fallido con el motivo del fallo. */
    public static ResultadoLogin fallido(EstadoLogin estado) {
        return new ResultadoLogin(estado, null, null);
    }

    public EstadoLogin getEstado() { return estado; }

    /** El token JWT. Solo tiene valor si el estado es EXITOSO. */
    public String getToken() { return token != null ? token : ""; }

    /** El rol ("USER" o "ADMIN"). Solo tiene valor si el estado es EXITOSO. */
    public String getRol() { return rol != null ? rol : ""; }
}
