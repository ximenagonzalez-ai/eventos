import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Auditoria } from '../models/auditoriamodel';

@Injectable({
  providedIn: 'root',
})
export class AuditoriaService {
  private apiUrl = 'http://localhost:8080/api/auditorias';

  constructor(private http: HttpClient) {}

  obtenerAuditoriasPorUsuario(usuarioId: number): Observable<Auditoria[]> {
    return this.http.get<Auditoria[]>(`${this.apiUrl}/usuario/${usuarioId}`);
  }
}
