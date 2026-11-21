import { ChangeDetectorRef, Component, DestroyRef, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize, timeout } from 'rxjs';
import { AuditoriaDTO } from '../../models/auditoriamodel';
import { ReporteDTO } from '../../models/reportemodel';
import { AuditoriaService } from '../../services/auditoriaservice';
import { ReporteService } from '../../services/reporteservice';
import { obtenerMensajeError } from '../../utils/errorhelper';

@Component({
  selector: 'app-auditoriacomponent',
  standalone: false,
  templateUrl: './auditoriacomponent.html',
  styleUrls: ['./auditoriacomponent.css']
})
export class AuditoriaComponent implements OnInit {
  usuarioIdFiltro = 1;
  auditorias: AuditoriaDTO[] = [];
  reportes: ReporteDTO[] = [];

  cargandoAuditoria = false;
  cargandoReportes = false;
  error = '';

  constructor(
    private auditoriaService: AuditoriaService,
    private reporteService: ReporteService,
    private destroyRef: DestroyRef,
    private cdr: ChangeDetectorRef
  ) {}

  get isAdmin(): boolean {
    return localStorage.getItem('rol') === 'ADMIN';
  }

  ngOnInit(): void {
    this.cargarReportes();
    if (this.isAdmin) {
      this.cargarAuditoria();
    }
  }

  cargarAuditoria(): void {
    if (!this.usuarioIdFiltro || this.usuarioIdFiltro <= 0) {
      this.error = 'Ingresa un ID de usuario válido para consultar la auditoría.';
      return;
    }

    this.cargandoAuditoria = true;
    this.error = '';

    this.auditoriaService.listarPorUsuario(this.usuarioIdFiltro).pipe(
      timeout(30_000),
      finalize(() => {
        this.cargandoAuditoria = false;
        if (!this.destroyRef.destroyed) this.cdr.detectChanges();
      }),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (data) => {
        this.auditorias = data || [];
      },
      error: (err) => {
        this.error = obtenerMensajeError(err, 'No se pudo cargar la auditoría.');
        this.auditorias = [];
      }
    });
  }

  cargarReportes(): void {
    this.cargandoReportes = true;
    this.error = '';

    this.reporteService.ventasPorEvento().pipe(
      timeout(30_000),
      finalize(() => {
        this.cargandoReportes = false;
        if (!this.destroyRef.destroyed) this.cdr.detectChanges();
      }),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (data) => {
        this.reportes = data || [];
      },
      error: (err) => {
        this.error = obtenerMensajeError(err, 'No se pudieron cargar los reportes.');
        this.reportes = [];
      }
    });
  }
}
