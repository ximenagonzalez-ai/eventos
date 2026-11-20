import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Usuario } from '../models/usermodel';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {
  private apiUrl = 'http://localhost:8080/user';

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

  obtenerTodos(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(`${this.apiUrl}/getall`, { headers: this.getHeaders() });
  }

  obtenerPorId(id: number): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.apiUrl}/getbyid/${id}`, { headers: this.getHeaders() });
  }

  crearUsuario(usuario: Usuario): Observable<string> {
    return this.http.post(`${this.apiUrl}/create`, usuario, {
      headers: this.getHeaders(),
      responseType: 'text'
    });
  }

  actualizarUsuario(id: number, usuario: Usuario): Observable<string> {
    return this.http.put(`${this.apiUrl}/update/${id}`, usuario, {
      headers: this.getHeaders(),
      responseType: 'text'
    });
  }

  eliminarPorId(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/deletebyid/${id}`, {
      headers: this.getHeaders(),
      responseType: 'text'
    });
  }
}
