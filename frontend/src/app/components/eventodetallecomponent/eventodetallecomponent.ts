
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { finalize, timeout } from 'rxjs';
import { EventoService } from '../../services/eventoservice';
import { Evento } from '../../models/eventomodel';
import { obtenerMensajeError } from '../../utils/errorhelper';

@Component({
  selector: 'app-evento-detalle',
  templateUrl: './eventodetallecomponent.html',
  styleUrls: ['./eventodetallecomponent.css'],
  standalone: false
})
export class EventoDetalleComponent implements OnInit {

  evento: Evento | null = null;
  isLoading: boolean = true;
  error: string = '';

  constructor(
    private route: ActivatedRoute,
    private eventoService: EventoService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = Number(params.get('id'));

      if (!id || id <= 0) {
        this.evento = null;
        this.error = 'El identificador del evento no es válido.';
        this.isLoading = false;
        return;
      }

      this.cargarEvento(id);
    });
  }

  cargarEvento(id: number): void {
    this.isLoading = true;
    this.error = '';
    this.evento = null;

    this.eventoService.obtenerPorId(id)
      .pipe(
        timeout(30000),
        finalize(() => {
          this.isLoading = false;
        })
      )
      .subscribe({
        next: (data) => {
          if (data) {
            this.evento = data;

            console.log('Detalle del evento recibido:', data);
          } else {
            this.error = 'No se encontró el evento solicitado.';
          }
        },
        error: (err) => {
          console.error('Error cargando detalle del evento:', err);

          this.error = obtenerMensajeError(
            err,
            'No se pudieron cargar los detalles del evento.'
          );
        }
      });
  }

  abrirMapa(): void {
    if (!this.evento || !this.evento.lugar) {
      return;
    }

    const lugar = this.evento.lugar;

    let busqueda = '';

    if (
      lugar.latitud != null &&
      lugar.longitud != null &&
      (lugar.latitud !== 0 || lugar.longitud !== 0)
    ) {
      busqueda = `${lugar.latitud},${lugar.longitud}`;
    } else {
      busqueda = [
        lugar.nombre,
        lugar.direccion,
        lugar.ciudad
      ].filter(Boolean).join(', ');
    }

    const url =
      'https://www.google.com/maps/search/?api=1&query=' +
      encodeURIComponent(busqueda);

    window.open(url, '_blank', 'noopener,noreferrer');
  }
}
