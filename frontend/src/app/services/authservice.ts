import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
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

  constructor(private http: HttpClient) {}

  login(data: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, data);
  }

  registro(data: RegistroRequest): Observable<Usuario> {
    return this.http.post<Usuario>(`${this.apiUrl}/registro`, data);
  }

  verificarEmail(token: string): Observable<AuthMessageResponse> {
    return this.http.post<AuthMessageResponse>(`${this.apiUrl}/verificar-email?token=${token}`, {});
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
