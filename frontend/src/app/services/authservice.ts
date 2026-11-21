import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, ReplaySubject, share, timeout } from 'rxjs';
import {
  LoginRequest,
  LoginResponse,
  RegistroRequest,
  OlvidePasswordRequest,
  RestablecimientoPasswordRequest,
  ReenviarVerificacionRequest,
  AuthMessageResponse
} from '../models/authmodel';
import { Usuario } from '../models/usermodel';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';
  private readonly verificationRequests = new Map<string, Observable<AuthMessageResponse>>();

  constructor(private http: HttpClient) {}

  login(data: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, data);
  }

  registro(data: RegistroRequest): Observable<Usuario> {
    return this.http.post<Usuario>(`${this.apiUrl}/registro`, data);
  }

  verificarEmail(token: string): Observable<AuthMessageResponse> {
    const tokenNormalizado = token.trim();
    const solicitudExistente = this.verificationRequests.get(tokenNormalizado);
    if (solicitudExistente) {
      return solicitudExistente;
    }

    const params = new HttpParams().set('token', tokenNormalizado);
    const solicitud = this.http
      .post<AuthMessageResponse>(`${this.apiUrl}/verificar-email`, {}, { params })
      .pipe(
        timeout({ first: 20_000 }),
        share({
          connector: () => new ReplaySubject<AuthMessageResponse>(1),
          resetOnError: false,
          resetOnComplete: false,
          resetOnRefCountZero: false,
        })
      );

    this.verificationRequests.set(tokenNormalizado, solicitud);
    return solicitud;
  }

  reenviarVerificacion(data: ReenviarVerificacionRequest): Observable<AuthMessageResponse> {
    return this.http.post<AuthMessageResponse>(`${this.apiUrl}/reenviar-verificacion`, data);
  }

  olvidePassword(data: OlvidePasswordRequest): Observable<AuthMessageResponse> {
    return this.http.post<AuthMessageResponse>(`${this.apiUrl}/olvide-password`, data);
  }

  restablecerPassword(data: RestablecimientoPasswordRequest): Observable<AuthMessageResponse> {
    return this.http.post<AuthMessageResponse>(`${this.apiUrl}/restablecer-password`, data);
  }
}
