export interface AuditoriaDTO {
  id: number;
  usuarioId?: number;
  accion: string;
  entidad: string;
  entidadId?: number;
  detalle?: string;
  fechaRegistro?: string;
}
