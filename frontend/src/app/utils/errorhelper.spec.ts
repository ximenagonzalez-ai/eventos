import { HttpErrorResponse } from '@angular/common/http';
import { TimeoutError } from 'rxjs';
import { obtenerCodigoError, obtenerMensajeError } from './errorhelper';

describe('errorhelper', () => {
  it('uses the backend message and extracts its code for a 400 response', () => {
    const error = new HttpErrorResponse({
      status: 400,
      error: { mensaje: 'Este enlace ya fue utilizado', codigo: 'TOKEN_USADO' },
    });

    expect(obtenerMensajeError(error, 'Mensaje de respaldo')).toBe('Este enlace ya fue utilizado');
    expect(obtenerCodigoError(error)).toBe('TOKEN_USADO');
  });

  it('provides clear messages for authorization and server errors', () => {
    expect(obtenerMensajeError(new HttpErrorResponse({ status: 401 }), 'fallback'))
      .toContain('sesión');
    expect(obtenerMensajeError(new HttpErrorResponse({ status: 403 }), 'fallback'))
      .toContain('permisos');
    expect(obtenerMensajeError(new HttpErrorResponse({ status: 500 }), 'fallback'))
      .toContain('error interno');
  });

  it('provides clear messages for network and timeout errors', () => {
    expect(obtenerMensajeError(new HttpErrorResponse({ status: 0 }), 'fallback'))
      .toContain('conectar');
    expect(obtenerMensajeError(new TimeoutError(), 'fallback'))
      .toContain('demasiado');
  });
});
