import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Categoria } from '../models/categoriamodel';

@Injectable({
  providedIn: 'root'
})
export class CategoriaService {
  private apiUrl = 'http://localhost:8080/categoria';

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

  obtenerTodas(): Observable<Categoria[]> {
    return this.http.get<Categoria[]>(`${this.apiUrl}/getall`, { headers: this.getHeaders() });
  }

  obtenerPorId(id: number): Observable<Categoria> {
    return this.http.get<Categoria>(`${this.apiUrl}/getbyid/${id}`, { headers: this.getHeaders() });
  }

  crear(categoria: Categoria): Observable<string> {
    return this.http.post(`${this.apiUrl}/create`, categoria, {
      headers: this.getHeaders(),
      responseType: 'text'
    });
  }

  actualizar(id: number, categoria: Categoria): Observable<string> {
    return this.http.put(`${this.apiUrl}/update/${id}`, categoria, {
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
