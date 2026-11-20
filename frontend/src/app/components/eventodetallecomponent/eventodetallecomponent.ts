import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { finalize, timeout } from 'rxjs';
import { EventoService } from '../../services/eventoservice';
import { Evento, TipoBoletaResumen } from '../../models/eventomodel';
import { BoletaService } from '../../services/boletaservice';
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

  // Compra de boletas
  cantidades: { [tipoId: number]: number } = {};
  comprandoId: number | null = null;
  errorCompra: string = '';
  mensajeCompra: string = '';

  constructor(
    private route: ActivatedRoute,
    private eventoService: EventoService,
    private cdr: ChangeDetectorRef,
    private boletaService: BoletaService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = Number(params.get('id'));

      if (!id || id <= 0) {
        this.evento = null;
        this.error = 'El identificador del evento no es válido.';
        this.isLoading = false;
        this.cdr.detectChanges();
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
          this.cdr.detectChanges();
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

  cantidadDe(tb: TipoBoletaResumen): number {
    return (tb.id != null && this.cantidades[tb.id]) || 1;
  }

  cambiarCantidad(tb: TipoBoletaResumen, delta: number): void {
    if (tb.id == null) return;
    const max = tb.cantidadDisponible ?? 10;
    const nueva = this.cantidadDe(tb) + delta;
    this.cantidades[tb.id] = Math.min(Math.max(nueva, 1), Math.min(max, 10));
  }

  comprar(tb: TipoBoletaResumen): void {
    this.errorCompra = '';
    this.mensajeCompra = '';

    const token = localStorage.getItem('token');
    const usuarioId = Number(localStorage.getItem('usuarioId'));

    if (!token || !usuarioId) {
      this.router.navigate(['/login']);
      return;
    }
    if (tb.id == null) {
      this.errorCompra = 'Este tipo de boleta no se puede comprar.';
      return;
    }

    const cantidad = this.cantidadDe(tb);
    this.comprandoId = tb.id;
    this.cdr.detectChanges();

    this.boletaService.comprarBoletas({
      usuarioId,
      tipoBoletaId: tb.id,
      cantidad
    })
      .pipe(
        timeout(60000),
        finalize(() => {
          this.comprandoId = null;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: () => {
          if (tb.cantidadDisponible != null) {
            tb.cantidadDisponible -= cantidad;
          }
          this.mensajeCompra = 'Compra realizada. Te llevamos a Mis Boletas...';
          this.cdr.detectChanges();
          setTimeout(() => this.router.navigate(['/mis-boletas']), 1200);
        },
        error: (err) => {
          this.errorCompra = obtenerMensajeError(
            err,
            'No se pudo completar la compra. Intenta de nuevo.'
          );
          this.cdr.detectChanges();
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
