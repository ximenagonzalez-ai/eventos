import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Evento, EstadoEvento } from '../models/eventomodel';

@Injectable({
  providedIn: 'root'
})
export class EventoService {
  private apiUrl = 'http://localhost:8080/evento';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    const headers: Record<string, string> = {
      'Content-Type': 'application/json'
    };

    if (token && token !== 'null' && token !== 'undefined' && token.trim() !== '') {
      headers['Authorization'] = `Bearer ${token}`;
    }

    return new HttpHeaders(headers);
  }

  obtenerTodos(): Observable<Evento[]> {
    return this.http.get<Evento[]>(`${this.apiUrl}/getall`, { headers: this.getHeaders() });
  }

  obtenerPorId(id: number): Observable<Evento> {
    return this.http.get<Evento>(`${this.apiUrl}/getbyid/${id}`, { headers: this.getHeaders() });
  }

  obtenerPorCategoria(categoriaId: number): Observable<Evento[]> {
    return this.http.get<Evento[]>(`${this.apiUrl}/bycategoria/${categoriaId}`, {
      headers: this.getHeaders()
    });
  }

  obtenerPorEstado(estado: EstadoEvento): Observable<Evento[]> {
    return this.http.get<Evento[]>(`${this.apiUrl}/byestado?estado=${estado}`, {
      headers: this.getHeaders()
    });
  }

  obtenerPorCiudad(ciudad: string): Observable<Evento[]> {
    return this.http.get<Evento[]>(`${this.apiUrl}/bycity?ciudad=${encodeURIComponent(ciudad)}`, {
      headers: this.getHeaders()
    });
  }

  buscarPorNombre(nombre: string): Observable<Evento[]> {
    return this.http.get<Evento[]>(`${this.apiUrl}/search?nombre=${encodeURIComponent(nombre)}`, {
      headers: this.getHeaders()
    });
  }

  crear(evento: Evento): Observable<string> {
    return this.http.post(`${this.apiUrl}/create`, evento, {
      headers: this.getHeaders(),
      responseType: 'text'
    });
  }

  actualizar(id: number, evento: Evento): Observable<string> {
    return this.http.put(`${this.apiUrl}/update/${id}`, evento, {
      headers: this.getHeaders(),
      responseType: 'text'
    });
  }

  eliminar(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/delete/${id}`, {
      headers: this.getHeaders(),
      responseType: 'text'
    });
  }

  sincronizarTicketmaster(): Observable<string> {
    return this.http.post(`${this.apiUrl}/sync`, {}, {
      headers: this.getHeaders(),
      responseType: 'text'
    });
  }
}
