import { ChangeDetectorRef, Component, DestroyRef, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize, timeout } from 'rxjs';
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
  descargandoPdfId: number | null = null;
  error = '';

  constructor(
    private itinerarioService: ItinerarioService,
    private destroyRef: DestroyRef,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarItinerarios();
  }

  cargarItinerarios(): void {
    this.cargando = true;
    this.error = '';

    this.itinerarioService.listarPorUsuario(this.usuarioId).pipe(
      timeout(30_000),
      finalize(() => {
        this.cargando = false;
        if (!this.destroyRef.destroyed) this.cdr.detectChanges();
      }),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (data) => {
        this.itinerarios = data || [];
      },
      error: (err) => {
        this.error = obtenerMensajeError(err, 'No se pudieron cargar los itinerarios.');
        this.itinerarios = [];
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
    this.itinerarioService.crear(this.usuarioId, nombre).pipe(
      timeout(30_000),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (creado) => {
        this.itinerarios = [creado, ...this.itinerarios];
        this.nombreNuevoItinerario = '';
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = obtenerMensajeError(err, 'No se pudo crear el itinerario.');
        this.cdr.detectChanges();
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
    this.itinerarioService.agregarEvento(itinerarioId, eventoId).pipe(
      timeout(30_000),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (actualizado) => {
        this.itinerarios = this.itinerarios.map((item) =>
          item.id === itinerarioId ? actualizado : item
        );
        this.eventoIdPorItinerario[itinerarioId] = null;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = obtenerMensajeError(err, 'No se pudo agregar el evento al itinerario.');
        this.cdr.detectChanges();
      }
    });
  }

  descargarPdf(itinerario: ItinerarioDTO): void {
    this.error = '';
    this.descargandoPdfId = itinerario.id;

    this.itinerarioService.descargarPdf(itinerario.id).pipe(
      timeout(30_000),
      finalize(() => {
        this.descargandoPdfId = null;
        if (!this.destroyRef.destroyed) this.cdr.detectChanges();
      }),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (pdf) => {
        const url = URL.createObjectURL(pdf);
        const enlace = document.createElement('a');
        enlace.href = url;
        enlace.download = `itinerario-${itinerario.id}.pdf`;
        document.body.appendChild(enlace);
        enlace.click();
        enlace.remove();
        window.setTimeout(() => URL.revokeObjectURL(url), 1000);
      },
      error: (err: unknown) => {
        this.error = obtenerMensajeError(err, 'No se pudo descargar el PDF del itinerario.');
        this.cdr.detectChanges();
      }
    });
  }
}
