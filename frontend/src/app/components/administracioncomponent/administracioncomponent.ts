import { ChangeDetectorRef, Component, DestroyRef, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize, timeout } from 'rxjs';
import { Categoria } from '../../models/categoriamodel';
import { CategoriaService } from '../../services/categoriaservice';
import { obtenerMensajeError } from '../../utils/errorhelper';

@Component({
  selector: 'app-administracion',
  templateUrl: './administracioncomponent.html',
  styleUrls: ['./administracioncomponent.css'],
  standalone: false
})
export class AdministracionComponent implements OnInit {
  categorias: Categoria[] = [];
  nombre = '';
  descripcion = '';
  categoriaEditandoId: number | null = null;
  cargando = false;
  guardando = false;
  mensaje = '';
  error = '';

  constructor(
    private categoriaService: CategoriaService,
    private destroyRef: DestroyRef,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarCategorias();
  }

  cargarCategorias(): void {
    this.cargando = true;
    this.error = '';

    this.categoriaService.obtenerTodas().pipe(
      timeout(30_000),
      finalize(() => {
        this.cargando = false;
        if (!this.destroyRef.destroyed) this.cdr.detectChanges();
      }),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (categorias) => {
        this.categorias = Array.isArray(categorias) ? categorias : [];
      },
      error: (err: unknown) => {
        this.categorias = [];
        this.error = obtenerMensajeError(err, 'No se pudieron cargar los tipos de evento.');
      }
    });
  }

  guardarCategoria(): void {
    const nombre = this.nombre.trim();
    if (!nombre) {
      this.error = 'Escribe el nombre del tipo de evento.';
      return;
    }

    this.guardando = true;
    this.error = '';
    this.mensaje = '';

    const categoria: Categoria = {
      nombre,
      descripcion: this.descripcion.trim() || undefined
    };
    const solicitud = this.categoriaEditandoId === null
      ? this.categoriaService.crear(categoria)
      : this.categoriaService.actualizar(this.categoriaEditandoId, categoria);

    solicitud.pipe(
      timeout(30_000),
      finalize(() => {
        this.guardando = false;
        if (!this.destroyRef.destroyed) this.cdr.detectChanges();
      }),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (respuesta) => {
        this.mensaje = respuesta;
        this.cancelarEdicion();
        this.cargarCategorias();
      },
      error: (err: unknown) => {
        this.error = obtenerMensajeError(
          err,
          'No se pudo guardar el tipo de evento.'
        );
      }
    });
  }

  editarCategoria(categoria: Categoria): void {
    if (categoria.id == null) return;
    this.categoriaEditandoId = categoria.id;
    this.nombre = categoria.nombre;
    this.descripcion = categoria.descripcion || '';
    this.mensaje = '';
    this.error = '';
  }

  cancelarEdicion(): void {
    this.categoriaEditandoId = null;
    this.nombre = '';
    this.descripcion = '';
  }
}
