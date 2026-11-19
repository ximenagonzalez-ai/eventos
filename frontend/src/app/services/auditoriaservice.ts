import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuditoriaDTO } from '../models/auditoriamodel';

@Injectable({
  providedIn: 'root'
})
export class AuditoriaService {
  private baseUrl = 'http://localhost:8080/auditoria';

  constructor(private http: HttpClient) {}

  private obtenerHeaders(): { headers?: HttpHeaders } {
    const token = localStorage.getItem('token');
    if (!token) {
      return {};
    }
    return {
      headers: new HttpHeaders({
        Authorization: `Bearer ${token}`
      })
    };
  }

  listarPorUsuario(usuarioId: number): Observable<AuditoriaDTO[]> {
    return this.http.get<AuditoriaDTO[]>(
      `${this.baseUrl}/usuario/${usuarioId}`,
      this.obtenerHeaders()
    );
  }
}
