import { Injectable } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';

/**
 * Agrega el header "Authorization: Bearer <token>" a todas las peticiones al backend,
 * menos a /api/auth/ (login, registro, etc.) y a las que ya lo traen.
 */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const esBackend = req.url.startsWith('http://localhost:8080');
    const esAuth = req.url.includes('/api/auth/');

    if (!esBackend || esAuth || req.headers.has('Authorization')) {
      return next.handle(req);
    }

    let token: string | null = null;
    try {
      token = localStorage.getItem('token');
    } catch {
      token = null;
    }

    if (!token) {
      return next.handle(req);
    }

    return next.handle(req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }));
  }
}
