import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ItinerarioDTO } from '../models/itinerariomodel';

@Injectable({
  providedIn: 'root'
})
export class ItinerarioService {
  private baseUrl = 'http://localhost:8080/api/itinerarios';

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

  listarPorUsuario(usuarioId: number): Observable<ItinerarioDTO[]> {
    return this.http.get<ItinerarioDTO[]>(
      `${this.baseUrl}/usuario/${usuarioId}`,
      this.obtenerHeaders()
    );
  }

  crear(usuarioId: number, nombre: string): Observable<ItinerarioDTO> {
    return this.http.post<ItinerarioDTO>(
      `${this.baseUrl}`,
      { usuarioId, nombre },
      this.obtenerHeaders()
    );
  }

  agregarEvento(itinerarioId: number, eventoId: number): Observable<ItinerarioDTO> {
    return this.http.post<ItinerarioDTO>(
      `${this.baseUrl}/${itinerarioId}/eventos/${eventoId}`,
      {},
      this.obtenerHeaders()
    );
  }
}
