import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Lugar } from '../models/lugarmodel';

@Injectable({
  providedIn: 'root'
})
export class LugarService {
  private apiUrl = 'http://localhost:8080/lugar';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token') || '';
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  obtenerTodos(): Observable<Lugar[]> {
    return this.http.get<Lugar[]>(`${this.apiUrl}/getall`, { headers: this.getHeaders() });
  }

  obtenerPorId(id: number): Observable<Lugar> {
    return this.http.get<Lugar>(`${this.apiUrl}/getbyid/${id}`, { headers: this.getHeaders() });
  }

  obtenerPorCiudad(ciudad: string): Observable<Lugar[]> {
    return this.http.get<Lugar[]>(`${this.apiUrl}/bycity?ciudad=${encodeURIComponent(ciudad)}`, {
      headers: this.getHeaders()
    });
  }

  crear(lugar: Lugar): Observable<string> {
    return this.http.post(`${this.apiUrl}/create`, lugar, {
      headers: this.getHeaders(),
      responseType: 'text'
    });
  }

  actualizar(id: number, lugar: Lugar): Observable<string> {
    return this.http.put(`${this.apiUrl}/update/${id}`, lugar, {
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
}
