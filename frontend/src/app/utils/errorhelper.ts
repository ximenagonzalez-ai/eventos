import { HttpErrorResponse } from '@angular/common/http';
import { TimeoutError } from 'rxjs';

export function obtenerMensajeError(err: unknown, mensajePorDefecto: string): string {
  if (err instanceof TimeoutError) {
    return 'El servidor tardó demasiado en responder. Inténtalo de nuevo.';
  }

  if (!(err instanceof HttpErrorResponse)) {
    return mensajePorDefecto;
  }

  const cuerpo = err.error;
  if (typeof cuerpo === 'object' && cuerpo !== null) {
    if ('mensaje' in cuerpo && typeof cuerpo.mensaje === 'string' && cuerpo.mensaje.trim()) {
      return cuerpo.mensaje.trim();
    }
    if ('message' in cuerpo && typeof cuerpo.message === 'string' && cuerpo.message.trim()) {
      return cuerpo.message.trim();
    }
  }
  if (typeof cuerpo === 'string' && cuerpo.trim()) {
    return cuerpo.trim();
  }

  if (err.status === 0) {
    return 'No se pudo conectar con el servidor. Revisa la conexión o intenta más tarde.';
  }
  if (err.status === 400) {
    return 'Los datos enviados no son válidos (400).';
  }
  if (err.status === 401) {
    return 'Tu sesión no es válida o expiró, o las credenciales son incorrectas (401).';
  }
  if (err.status === 403) {
    return 'No tienes permisos para realizar esta acción (403).';
  }
  if (err.status === 404) {
    return 'El recurso solicitado no fue encontrado (404).';
  }
  if (err.status === 409) {
    return 'Ya existe un registro con esos datos (409).';
  }
  if (err.status >= 500) {
    return 'Ocurrió un error interno en el servidor (500).';
  }

  return mensajePorDefecto;
}

export function obtenerCodigoError(err: unknown): string {
  if (!(err instanceof HttpErrorResponse)) {
    return '';
  }

  const cuerpo = err.error;
  return typeof cuerpo === 'object' && cuerpo !== null
    && 'codigo' in cuerpo && typeof cuerpo.codigo === 'string'
    ? cuerpo.codigo.trim()
    : '';
}
