import { ChangeDetectorRef, Component, DestroyRef, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize, timeout } from 'rxjs';
import { Boleta, CompraDTO } from '../../models/boletamodel';
import { BoletaService } from '../../services/boletaservice';
import { obtenerMensajeError } from '../../utils/errorhelper';

@Component({
  selector: 'app-miboletacomponent',
  standalone: false,
  templateUrl: './miboletacomponent.html',
  styleUrls: ['./miboletacomponent.css']
})
export class MiBoletaComponent implements OnInit {
  usuarioId = Number(localStorage.getItem('usuarioId')) || 0;
  esAdmin = false;
  mostrarModal = false;

  compras: CompraDTO[] = [];
  boletas: Boleta[] = [];
  boletasPorCompra: Record<number, Boleta[]> = {};

  nuevaBoleta: { eventoNombre: string; tipoBoleta: string; precio: number } = {
    eventoNombre: '',
    tipoBoleta: 'General',
    precio: 0
  };

  cargando = false;
  error = '';

  constructor(
    private boletaService: BoletaService,
    private cdr: ChangeDetectorRef,
    private destroyRef: DestroyRef
  ) {}

  ngOnInit(): void {
    this.cargarCompras();
  }

  abrirModal(): void {
    this.mostrarModal = true;
  }

  cerrarModal(): void {
    this.mostrarModal = false;
  }

  crearBoleta(): void {
    if (!this.nuevaBoleta.eventoNombre.trim()) {
      this.error = 'Ingresa el nombre del evento para la boleta.';
      return;
    }

    this.error = '';
    const creada: Boleta = {
      id: Date.now(),
      codigo: `BOL-${Math.floor(100000 + Math.random() * 900000)}`,
      eventoNombre: this.nuevaBoleta.eventoNombre.trim(),
      tipoBoleta: this.nuevaBoleta.tipoBoleta,
      precio: Number(this.nuevaBoleta.precio) || 0,
      fechaEmision: new Date().toISOString().slice(0, 16).replace('T', ' '),
      estado: 'ACTIVA'
    };

    this.boletas = [creada, ...this.boletas];
    this.nuevaBoleta = {
      eventoNombre: '',
      tipoBoleta: 'General',
      precio: 0
    };
    this.cerrarModal();
  }

  cargarCompras(): void {
    if (!this.usuarioId) {
      this.error = 'Inicia sesión para ver tus boletas.';
      return;
    }

    this.cargando = true;
    this.error = '';

    this.boletaService.listarComprasPorUsuario(this.usuarioId).pipe(
      timeout(30_000),
      finalize(() => {
        this.cargando = false;
        if (!this.destroyRef.destroyed) this.cdr.detectChanges();
      }),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (compras) => {
        this.compras = compras;
        this.boletas = [];
        this.boletasPorCompra = {};

        compras.forEach((c) => this.cargarBoletas(c.id, c.estado));
      },
      error: (err) => {
        this.error = obtenerMensajeError(err, 'No se pudieron cargar tus compras.');
        this.compras = [];
        this.boletas = [];
        this.boletasPorCompra = {};
      }
    });
  }

  cargarBoletas(compraId: number, estadoCompra?: string): void {
    this.boletaService.listarBoletasPorCompra(compraId, estadoCompra).pipe(
      timeout(30_000),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (boletas) => {
        this.boletasPorCompra[compraId] = boletas;
        this.boletas = [...this.boletas, ...boletas];
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.boletasPorCompra[compraId] = [];
        this.error = obtenerMensajeError(err, 'No se pudieron cargar algunas boletas.');
        this.cdr.detectChanges();
        this.cdr.detectChanges();
      }
    });
  }

  cancelarBoleta(id?: number): void {
    if (!id) {
      return;
    }

    this.error = '';
    let compraIdEncontrada = id;
    for (const [key, lista] of Object.entries(this.boletasPorCompra)) {
      if (lista.some((b) => b.id === id)) {
        compraIdEncontrada = Number(key);
        break;
      }
    }

    this.boletaService.cancelarCompra(compraIdEncontrada).pipe(
      timeout(30_000),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: () => {
        this.boletas = this.boletas.map((b) =>
          b.id === id ? { ...b, estado: 'CANCELADA' } : b
        );
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = obtenerMensajeError(err, 'No se pudo cancelar la compra.');
        this.cdr.detectChanges();
      }
    });
  }

  descargarPdfBoleta(): void {
    window.print();
  }
}
