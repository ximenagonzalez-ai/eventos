import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReporteDTO } from '../models/reportemodel';

@Injectable({
  providedIn: 'root'
})
export class ReporteService {
  private baseUrl = 'http://localhost:8080/api/reportes';

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

  ventasPorEvento(): Observable<ReporteDTO[]> {
    return this.http.get<ReporteDTO[]>(
      `${this.baseUrl}/ventas-por-evento`,
      this.obtenerHeaders()
    );
  }
}
