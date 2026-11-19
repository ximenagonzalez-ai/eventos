export interface ItinerarioDTO {
  id: number;
  usuarioId: number;
  nombre: string;
  fechaCreacion?: string;
  eventos: string[];
}
