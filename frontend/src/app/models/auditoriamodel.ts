export interface Auditoria {
  id?: number;
  usuarioId: number;
  accion: string;
  detalles?: string;
  fechaAccion: string;
}
