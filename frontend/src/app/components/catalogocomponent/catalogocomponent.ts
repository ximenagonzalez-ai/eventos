import { ChangeDetectorRef, Component, DestroyRef, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { distinctUntilChanged, finalize, map, switchMap, tap, timeout } from 'rxjs';
import { EventoService } from '../../services/eventoservice';
import { CategoriaService } from '../../services/categoriaservice';
import { Evento } from '../../models/eventomodel';
import { Categoria } from '../../models/categoriamodel';
import { obtenerMensajeError } from '../../utils/errorhelper';

@Component({
  selector: 'app-catalogo',
  templateUrl: './catalogocomponent.html',
  styleUrls: ['./catalogocomponent.css'],
  standalone: false
})
export class CatalogoComponent implements OnInit {

  eventos: Evento[] = [];
  categorias: Categoria[] = [];
  tituloSeccion = 'Todos los Eventos';
  categoriaSeleccionadaId: number | null = null;
  ciudadFiltro = '';
  busquedaTexto = '';
  isLoading = false;
  mensaje = '';
  error = '';
  errorCategorias = '';

  constructor(
    private route: ActivatedRoute,
    private eventoService: EventoService,
    private categoriaService: CategoriaService,
    private cdr: ChangeDetectorRef,
    private destroyRef: DestroyRef
  ) {}

  get isAdmin(): boolean {
    return localStorage.getItem('role') === 'ADMIN'
      || localStorage.getItem('rol') === 'ADMIN';
  }

  ngOnInit(): void {
    this.cargarCategoriasYConfigurarRuta();
  }

  cargarCategoriasYConfigurarRuta(): void {
    this.categoriaService.obtenerTodas()
      .pipe(
        timeout(30_000),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (cats) => {
          this.categorias = Array.isArray(cats) ? cats : [];
          this.errorCategorias = '';
          this.cdr.detectChanges();
          this.escucharRuta();
        },
        error: (err) => {
          this.categorias = [];
          this.errorCategorias = obtenerMensajeError(
            err,
            'No se pudieron cargar las categorías.'
          );
          this.cdr.detectChanges();
          this.escucharRuta();
        }
      });
  }

  escucharRuta(): void {
    this.route.url.pipe(
      map((segments) => segments[0]?.path || ''),
      distinctUntilChanged(),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe((path) => {
      const queryParam = this.route.snapshot.queryParamMap.get('q');

      if (queryParam) {
        this.busquedaTexto = queryParam;
        this.tituloSeccion = `Resultados para "${queryParam}"`;
        this.buscarPorNombre();
        return;
      }

      if (path === 'conciertos') {
        this.filtrarPorNombreCategoria('Concierto', 'Conciertos');
      } else if (path === 'deportes') {
        this.filtrarPorNombreCategoria('Deporte', 'Deportes');
      } else if (path === 'teatro') {
        this.filtrarPorNombreCategoria('Teatro', 'Teatro');
      } else {
        this.tituloSeccion = 'Explora Eventos en Vivo';
        this.categoriaSeleccionadaId = null;
        this.cargarTodos();
      }
    });
  }

  filtrarPorNombreCategoria(clave: string, titulo: string): void {
    this.tituloSeccion = titulo;

    const cat = this.categorias.find(c =>
      (c.nombre || '').toLowerCase().includes(clave.toLowerCase())
    );

    if (cat && cat.id != null) {
      this.filtrarPorCategoria(cat.id);
    } else {
      this.categoriaSeleccionadaId = null;
      this.cargarTodos();
    }
  }

  cargarTodos(): void {
    this.isLoading = true;
    this.mensaje = '';
    this.error = '';

    this.eventoService.obtenerTodos()
      .pipe(
        timeout(30000),
        finalize(() => {
          this.isLoading = false;
          if (!this.destroyRef.destroyed) this.cdr.detectChanges();
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (data) => {
          this.eventos = Array.isArray(data) ? data : [];
          console.log('Eventos recibidos:', this.eventos.length);
        },
        error: (err) => {
          this.eventos = [];
          console.error('Error obteniendo eventos:', err);
          this.error = obtenerMensajeError(
            err,
            'No se pudieron cargar los eventos.'
          );
        }
      });
  }

  filtrarPorCategoria(categoriaId: number | null): void {
    this.categoriaSeleccionadaId = categoriaId;

    if (categoriaId == null) {
      this.tituloSeccion = 'Explora Eventos en Vivo';
      this.cargarTodos();
      return;
    }

    const cat = this.categorias.find(c => c.id === categoriaId);
    if (cat) this.tituloSeccion = cat.nombre;

    this.isLoading = true;
    this.mensaje = '';
    this.error = '';

    this.eventoService.obtenerPorCategoria(categoriaId)
      .pipe(
        timeout(30000),
        finalize(() => {
          this.isLoading = false;
          if (!this.destroyRef.destroyed) this.cdr.detectChanges();
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (data) => {
          this.eventos = Array.isArray(data) ? data : [];
        },
        error: (err) => {
          this.eventos = [];
          this.error = obtenerMensajeError(
            err,
            'No se pudieron filtrar los eventos.'
          );
        }
      });
  }

  filtrarPorCiudad(): void {
    if (!this.ciudadFiltro.trim()) {
      this.cargarTodos();
      return;
    }

    this.isLoading = true;
    this.mensaje = '';
    this.error = '';

    this.eventoService.obtenerPorCiudad(this.ciudadFiltro)
      .pipe(
        timeout(30000),
        finalize(() => {
          this.isLoading = false;
          if (!this.destroyRef.destroyed) this.cdr.detectChanges();
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (data) => {
          this.eventos = Array.isArray(data) ? data : [];
        },
        error: (err) => {
          this.eventos = [];
          this.error = obtenerMensajeError(
            err,
            'No se pudieron filtrar los eventos por ciudad.'
          );
        }
      });
  }

  buscarPorNombre(): void {
    if (!this.busquedaTexto.trim()) {
      this.cargarTodos();
      return;
    }

    this.isLoading = true;
    this.mensaje = '';
    this.error = '';

    this.eventoService.buscarPorNombre(this.busquedaTexto)
      .pipe(
        timeout(30000),
        finalize(() => {
          this.isLoading = false;
          if (!this.destroyRef.destroyed) this.cdr.detectChanges();
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (data) => {
          this.eventos = Array.isArray(data) ? data : [];
        },
        error: (err) => {
          this.eventos = [];
          this.error = obtenerMensajeError(
            err,
            'No se pudo realizar la busqueda.'
          );
        }
      });
  }

  sincronizarTicketmaster(): void {
    this.isLoading = true;
    this.mensaje = '';
    this.error = '';

    this.eventoService.sincronizarTicketmaster()
      .pipe(
        timeout(120_000),
        tap((respuesta) => {
          this.mensaje = respuesta;
        }),
        switchMap(() => this.eventoService.obtenerTodos().pipe(timeout(30_000))),
        finalize(() => {
          this.isLoading = false;
          if (!this.destroyRef.destroyed) this.cdr.detectChanges();
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (data) => {
          this.eventos = Array.isArray(data) ? data : [];
        },
        error: (err) => {
          this.mensaje = '';
          this.error = obtenerMensajeError(
            err,
            'No se pudo sincronizar o actualizar los eventos.'
          );
        }
      });
  }
}
