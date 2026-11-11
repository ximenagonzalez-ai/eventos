import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  login(credentials: { email: string; password: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, credentials);
  }

  registro(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/registro`, userData);
  }

  verificarEmail(token: string): Observable<any> {
    const params = new HttpParams().set('token', token);
    return this.http.get(`${this.apiUrl}/verificar-email`, { params });
  }

  reenviarVerificacion(token: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/reenviar-verificacion`, { token });
  }
}
