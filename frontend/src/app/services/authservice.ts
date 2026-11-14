import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  LoginRequest,
  ReenviarVerificacionRequest,
  RegistroRequest,
  VerificacionResponse,
} from '../models/authmodel';
import { Usuario } from '../models/usermodel';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  login(credentials: LoginRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, credentials);
  }

  registro(userData: RegistroRequest): Observable<Usuario> {
    return this.http.post<Usuario>(`${this.apiUrl}/registro`, userData);
  }

  verificarEmail(token: string): Observable<VerificacionResponse> {
    const params = new HttpParams().set('token', token);
    return this.http.post<VerificacionResponse>(
      `${this.apiUrl}/verificar-email`,
      null,
      { params }
    );
  }

  reenviarVerificacion(
    data: ReenviarVerificacionRequest
  ): Observable<{ mensaje: string }> {
    return this.http.post<{ mensaje: string }>(
      `${this.apiUrl}/reenviar-verificacion`,
      data
    );
  }
}
