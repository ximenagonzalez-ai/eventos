import { Component, OnInit } from '@angular/core';
import { ItinerarioDTO } from '../../models/itinerariomodel';
import { ItinerarioService } from '../../services/itinerarioservice';
import { obtenerMensajeError } from '../../utils/errorhelper';

@Component({
  selector: 'app-itinerariocomponent',
  standalone: false,
  templateUrl: './itinerariocomponent.html',
  styleUrls: ['./itinerariocomponent.css']
})
export class ItinerarioComponent implements OnInit {
  usuarioId = Number(localStorage.getItem('usuarioId')) || 1;
  nombreNuevoItinerario = '';
  eventoIdPorItinerario: Record<number, number | null> = {};

  itinerarios: ItinerarioDTO[] = [];
  cargando = false;
  error = '';

  constructor(private itinerarioService: ItinerarioService) {}

  ngOnInit(): void {
    this.cargarItinerarios();
  }

  cargarItinerarios(): void {
    this.cargando = true;
    this.error = '';

    this.itinerarioService.listarPorUsuario(this.usuarioId).subscribe({
      next: (data) => {
        this.itinerarios = data || [];
        this.cargando = false;
      },
      error: (err) => {
        this.error = obtenerMensajeError(err, 'No se pudieron cargar los itinerarios.');
        this.cargando = false;
      }
    });
  }

  crearItinerario(): void {
    const nombre = this.nombreNuevoItinerario.trim();
    if (!nombre) {
      this.error = 'Escribe un nombre para el itinerario.';
      return;
    }

    this.error = '';
    this.itinerarioService.crear(this.usuarioId, nombre).subscribe({
      next: (creado) => {
        this.itinerarios = [creado, ...this.itinerarios];
        this.nombreNuevoItinerario = '';
      },
      error: (err) => {
        this.error = obtenerMensajeError(err, 'No se pudo crear el itinerario.');
      }
    });
  }

  agregarEvento(itinerarioId: number): void {
    const eventoId = this.eventoIdPorItinerario[itinerarioId];
    if (!eventoId || eventoId <= 0) {
      this.error = 'Ingresa un ID de evento válido.';
      return;
    }

    this.error = '';
    this.itinerarioService.agregarEvento(itinerarioId, eventoId).subscribe({
      next: (actualizado) => {
        this.itinerarios = this.itinerarios.map((item) =>
          item.id === itinerarioId ? actualizado : item
        );
        this.eventoIdPorItinerario[itinerarioId] = null;
      },
      error: (err) => {
        this.error = obtenerMensajeError(err, 'No se pudo agregar el evento al itinerario.');
      }
    });
  }
}
