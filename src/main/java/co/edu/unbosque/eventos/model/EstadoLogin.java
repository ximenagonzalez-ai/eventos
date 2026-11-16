package co.edu.unbosque.eventos.model;

/** Estados posibles al intentar hacer login. */
public enum EstadoLogin {
    /** Email y contraseña correctos, cuenta activa y verificada. */
    EXITOSO,
    /** Email no existe o contraseña incorrecta. */
    CREDENCIALES_INVALIDAS,
    /** El usuario no hizo clic en el enlace de verificación del correo. */
    CUENTA_NO_VERIFICADA,
    /** El admin desactivó la cuenta (activo = false). */
    CUENTA_BLOQUEADA
}
