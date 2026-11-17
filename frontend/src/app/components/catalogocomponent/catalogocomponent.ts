import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { EventoService } from '../../services/eventoservice';
import { CategoriaService } from '../../services/categoriaservice';
import { Evento } from '../../models/eventomodel';
import { Categoria } from '../../models/categoriamodel';

@Component({
  selector: 'app-catalogo',
  templateUrl: './catalogocomponent.html',
  styleUrls: ['./catalogocomponent.css'],
  standalone: false
})
export class CatalogoComponent implements OnInit {
  eventos: Evento[] = [];
  categorias: Categoria[] = [];
  tituloSeccion: string = 'Todos los Eventos';
  categoriaSeleccionadaId: number | null = null;
  ciudadFiltro: string = '';
  busquedaTexto: string = '';
  isLoading: boolean = false;
  mensaje: string = '';
  error: string = '';

  constructor(
    private route: ActivatedRoute,
    private eventoService: EventoService,
    private categoriaService: CategoriaService
  ) {}

  get isAdmin(): boolean {
    return localStorage.getItem('role') === 'ADMIN' || localStorage.getItem('rol') === 'ADMIN';
  }

  ngOnInit(): void {
    this.cargarCategoriasYConfigurarRuta();
  }

  cargarCategoriasYConfigurarRuta(): void {
    this.categoriaService.obtenerTodas().subscribe({
      next: (cats) => {
        this.categorias = cats || [];
        this.escucharRuta();
      },
      error: () => {
        this.escucharRuta();
      }
    });
  }

  escucharRuta(): void {
    this.route.url.subscribe(segments => {
      const path = segments[0]?.path || '';
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
      c.nombre.toLowerCase().includes(clave.toLowerCase())
    );
    if (cat && cat.id) {
      this.categoriaSeleccionadaId = cat.id;
      this.filtrarPorCategoria(cat.id);
    } else {
      this.cargarTodos();
    }
  }

  cargarTodos(): void {
    this.isLoading = true;
    this.error = '';
    this.eventoService.obtenerTodos().subscribe({
      next: (data) => {
        this.eventos = data || [];
        this.isLoading = false;
      },
      error: () => {
        this.error = 'No se pudieron cargar los eventos. Verifica que tengas sesión iniciada.';
        this.isLoading = false;
      }
    });
  }

  filtrarPorCategoria(categoriaId: number | null): void {
    this.categoriaSeleccionadaId = categoriaId;
    if (!categoriaId) {
      this.tituloSeccion = 'Explora Eventos en Vivo';
      this.cargarTodos();
      return;
    }

    const cat = this.categorias.find(c => c.id === categoriaId);
    if (cat) this.tituloSeccion = cat.nombre;

    this.isLoading = true;
    this.eventoService.obtenerPorCategoria(categoriaId).subscribe({
      next: (data) => {
        this.eventos = data || [];
        this.isLoading = false;
      },
      error: () => {
        this.eventos = [];
        this.isLoading = false;
      }
    });
  }

  filtrarPorCiudad(): void {
    if (!this.ciudadFiltro.trim()) {
      this.cargarTodos();
      return;
    }
    this.isLoading = true;
    this.eventoService.obtenerPorCiudad(this.ciudadFiltro).subscribe({
      next: (data) => {
        this.eventos = data || [];
        this.isLoading = false;
      },
      error: () => {
        this.eventos = [];
        this.isLoading = false;
      }
    });
  }

  buscarPorNombre(): void {
    if (!this.busquedaTexto.trim()) {
      this.cargarTodos();
      return;
    }
    this.isLoading = true;
    this.eventoService.buscarPorNombre(this.busquedaTexto).subscribe({
      next: (data) => {
        this.eventos = data || [];
        this.isLoading = false;
      },
      error: () => {
        this.eventos = [];
        this.isLoading = false;
      }
    });
  }

  sincronizarTicketmaster(): void {
    this.isLoading = true;
    this.mensaje = '';
    this.error = '';
    this.eventoService.sincronizarTicketmaster().subscribe({
      next: (res) => {
        this.mensaje = res;
        this.cargarTodos();
      },
      error: () => {
        this.error = 'Error al sincronizar eventos desde Ticketmaster.';
        this.isLoading = false;
      }
    });
  }
}
