export type EstadoBoleta = 'ACTIVA' | 'USADA' | 'CANCELADA';
export type EstadoCompra = 'PENDIENTE' | 'PAGADA' | 'CANCELADA' | string;

export interface Boleta {
  id?: number;
  codigo: string;
  eventoNombre: string;
  tipoBoleta: string;
  precio: number;
  fechaEmision: string;
  estado: EstadoBoleta;
}

export interface BoletaBackendDTO {
  id: number;
  compraId: number;
  codigo: string;
  tipoBoleta: string;
  evento: string;
  precioPagado: number;
  fechaEmision: string;
}

export interface CompraRequestDTO {
  usuarioId: number;
  tipoBoletaId: number;
  cantidad: number;
}

export interface CompraDTO {
  id: number;
  usuarioId: number;
  total: number;
  estado: EstadoCompra;
  fechaCompra: string;
}
