export function obtenerMensajeError(err: any, mensajePorDefecto: string): string {
  if (err?.status === 0) {
    return 'No se pudo conectar con el servidor. Revisa la URL, el puerto o que el backend esté encendido.';
  }

  if (typeof err?.error === 'string' && err.error.trim().length > 0) {
    return err.error;
  }

  const mensajeBackend = err?.error?.mensaje || err?.error?.message;
  if (mensajeBackend) {
    return mensajeBackend;
  }

  if (err?.status === 400) {
    return 'Los datos enviados no son válidos (400).';
  }
  if (err?.status === 401) {
    return 'Tu sesión no es válida o expiró o las credenciales son incorrectas (401).';
  }
  if (err?.status === 403) {
    return 'No tienes permisos para realizar esta acción (403).';
  }
  if (err?.status === 404) {
    return 'El recurso solicitado no fue encontrado (404).';
  }
  if (err?.status === 409) {
    return 'Ya existe un registro con esos datos (409).';
  }
  if (err?.status >= 500) {
    return 'Ocurrió un error interno en el servidor (500).';
  }

  return mensajePorDefecto;
}
