import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { EventoService } from '../../services/eventoservice';
import { Evento } from '../../models/eventomodel';

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
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.eventoService.obtenerPorId(id).subscribe({
        next: (data) => {
          this.evento = data;
          this.isLoading = false;
        },
        error: () => {
          this.error = 'No se encontró el evento solicitado.';
          this.isLoading = false;
        }
      });
    }
  }

  abrirMapa(): void {
    if (!this.evento?.lugar) return;
    const { latitud, longitud, direccion, ciudad } = this.evento.lugar;
    const url = (latitud && longitud)
      ? `https://www.google.com/maps/search/?api=1&query=${latitud},${longitud}`
      : `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(direccion + ', ' + ciudad)}`;
    window.open(url, '_blank');
  }
}
