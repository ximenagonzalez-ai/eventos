export interface Boleta {
  id?: number;
  codigo: string;
  eventoNombre: string;
  tipoBoleta: string;
  precio: number;
  fechaEmision: string;
  estado: 'ACTIVA' | 'USADA' | 'CANCELADA';
}
