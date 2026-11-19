import { Component, OnInit } from '@angular/core';
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
  usuarioId = 1;
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

  constructor(private boletaService: BoletaService) {}

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
    this.cargando = true;
    this.error = '';

    this.boletaService.listarComprasPorUsuario(this.usuarioId).subscribe({
      next: (compras) => {
        this.compras = compras;
        this.boletas = [];
        this.boletasPorCompra = {};
        this.cargando = false;

        compras.forEach((c) => this.cargarBoletas(c.id, c.estado));
      },
      error: (err) => {
        this.error = obtenerMensajeError(err, 'No se pudieron cargar tus compras.');
        this.cargando = false;
      }
    });
  }

  cargarBoletas(compraId: number, estadoCompra?: string): void {
    this.boletaService.listarBoletasPorCompra(compraId, estadoCompra).subscribe({
      next: (boletas) => {
        this.boletasPorCompra[compraId] = boletas;
        this.boletas = [...this.boletas, ...boletas];
      },
      error: (err) => {
        this.boletasPorCompra[compraId] = [];
        this.error = obtenerMensajeError(err, 'No se pudieron cargar algunas boletas.');
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

    this.boletaService.cancelarCompra(compraIdEncontrada).subscribe({
      next: () => {
        this.boletas = this.boletas.map((b) =>
          b.id === id ? { ...b, estado: 'CANCELADA' } : b
        );
      },
      error: (err) => {
        this.error = obtenerMensajeError(err, 'No se pudo cancelar la compra.');
      }
    });
  }

  descargarPdfBoleta(): void {
    window.print();
  }
}
