import { Lugar } from './lugarmodel';

export type EstadoEvento = 'ACTIVO' | 'CANCELADO' | 'FINALIZADO' | 'PROXIMO';
export type FuenteEvento = 'LOCAL' | 'EXTERNO';

export interface TipoBoletaResumen {
  id?: number;
  nombre: string;
  precio: number;
  cantidadDisponible?: number;
  cantidadTotal?: number;
  descripcion?: string;
}

export interface Evento {
  id?: number;
  nombre: string;
  descripcion: string;
  fechaInicio: string;
  fechaFin?: string;
  imagenUrl?: string;
  estado: EstadoEvento;
  fuente?: FuenteEvento;
  idexterno?: string;
  urlVentaExterna?: string;
  precioMinimoExterno?: number;
  precioMaximoExterno?: number;
  monedaPrecioExterno?: string;
  categoriaId?: number;
  categoriaNombre?: string;
  lugar?: Lugar;
  tiposBoleta?: TipoBoletaResumen[];
}
