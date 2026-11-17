package co.edu.unbosque.eventos.model;

/** Resultado del intento de restablecer la contraseña con el token recibido por correo. */
public enum ResultadoRestablecimiento {
    /** La contraseña fue cambiada exitosamente. */
    EXITOSO,
    /** El token no existe en la BD. */
    TOKEN_INVALIDO,
    /** El token ya fue usado anteriormente. */
    TOKEN_USADO,
    /** El token venció (duración: 30 minutos por defecto). */
    TOKEN_EXPIRADO
}
