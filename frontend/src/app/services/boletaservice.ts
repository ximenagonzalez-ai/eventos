import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import {
  Boleta,
  BoletaBackendDTO,
  CompraDTO,
  CompraRequestDTO,
  EstadoBoleta
} from '../models/boletamodel';

@Injectable({
  providedIn: 'root'
})
export class BoletaService {
  private readonly baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  private mapearBoleta(dto: BoletaBackendDTO, estadoCompra?: string): Boleta {
    let estado: EstadoBoleta = 'ACTIVA';
    if (estadoCompra === 'CANCELADA') {
      estado = 'CANCELADA';
    }
    return {
      id: dto.id,
      codigo: dto.codigo,
      eventoNombre: dto.evento,
      tipoBoleta: dto.tipoBoleta,
      precio: Number(dto.precioPagado),
      fechaEmision: dto.fechaEmision,
      estado
    };
  }

  comprarBoletas(payload: CompraRequestDTO): Observable<Boleta[]> {
    return this.http
      .post<BoletaBackendDTO[]>(`${this.baseUrl}/boletas/comprar`, payload)
      .pipe(map((items) => items.map((item) => this.mapearBoleta(item, 'ACTIVA'))));
  }

  listarComprasPorUsuario(usuarioId: number): Observable<CompraDTO[]> {
    return this.http.get<CompraDTO[]>(`${this.baseUrl}/compras/usuario/${usuarioId}`);
  }

  listarBoletasPorCompra(compraId: number, estadoCompra?: string): Observable<Boleta[]> {
    return this.http
      .get<BoletaBackendDTO[]>(`${this.baseUrl}/boletas/compra/${compraId}`)
      .pipe(map((items) => items.map((item) => this.mapearBoleta(item, estadoCompra))));
  }

  cancelarCompra(compraId: number): Observable<CompraDTO> {
    return this.http.post<CompraDTO>(`${this.baseUrl}/boletas/compra/${compraId}/cancelar`, {});
  }
}
