
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

    let headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    if (token && token !== 'null'
      && token !== 'undefined'
      && token.trim() !== '') {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }

    return headers;
  }

  obtenerTodos(): Observable<Evento[]> {
    return this.http.get<Evento[]>(`${this.apiUrl}/getall`);
  }

  obtenerPorId(id: number): Observable<Evento> {
    return this.http.get<Evento>(`${this.apiUrl}/getbyid/${id}`);
  }

  obtenerPorCategoria(categoriaId: number): Observable<Evento[]> {
    return this.http.get<Evento[]>(
      `${this.apiUrl}/bycategoria/${categoriaId}`
    );
  }

  obtenerPorEstado(estado: EstadoEvento): Observable<Evento[]> {
    return this.http.get<Evento[]>(
      `${this.apiUrl}/byestado?estado=${estado}`
    );
  }

  obtenerPorCiudad(ciudad: string): Observable<Evento[]> {
    return this.http.get<Evento[]>(
      `${this.apiUrl}/bycity?ciudad=${encodeURIComponent(ciudad)}`
    );
  }

  buscarPorNombre(nombre: string): Observable<Evento[]> {
    return this.http.get<Evento[]>(
      `${this.apiUrl}/search?nombre=${encodeURIComponent(nombre)}`
    );
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
