import { ChangeDetectorRef, Component, DestroyRef, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize, timeout } from 'rxjs';
import { UsuarioService } from '../../services/userservice';
import { Usuario } from '../../models/usermodel';
import { obtenerMensajeError } from '../../utils/errorhelper';

@Component({
  selector: 'app-perfil',
  templateUrl: './perfilcomponent.html',
  styleUrls: ['./perfilcomponent.css'],
  standalone: false
})
export class PerfilComponent implements OnInit {
  usuarios: Usuario[] = [];
  usuarioSeleccionado: Usuario | null = null;
  mensaje: string = '';
  error: string = '';
  cargando: boolean = false;

  constructor(
    private usuarioService: UsuarioService,
    private destroyRef: DestroyRef,
    private cdr: ChangeDetectorRef
  ) {}

  get isAdmin(): boolean {
    return localStorage.getItem('rol') === 'ADMIN';
  }

  ngOnInit(): void {
    this.cargarUsuarios();
  }

  cargarUsuarios(): void {
    this.cargando = true;
    this.mensaje = '';
    this.error = '';
    this.usuarioService.obtenerTodos().pipe(
      timeout(30_000),
      finalize(() => {
        this.cargando = false;
        if (!this.destroyRef.destroyed) this.cdr.detectChanges();
      }),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (data) => {
        this.usuarios = data || [];
        const emailActual = localStorage.getItem('email');
        if (emailActual) {
          this.usuarioSeleccionado = this.usuarios.find(u => u.email === emailActual) || this.usuarios[0] || null;
        } else {
          this.usuarioSeleccionado = this.usuarios[0] || null;
        }
      },
      error: (err) => {
        this.error = obtenerMensajeError(err, 'No se pudo cargar la información del usuario.');
        this.usuarios = [];
        this.usuarioSeleccionado = null;
      }
    });
  }

  eliminarUsuario(id?: number): void {
    if (!id || !confirm('¿Seguro que deseas eliminar este usuario?')) return;
    this.error = '';
    this.mensaje = '';
    this.usuarioService.eliminarPorId(id).pipe(
      timeout(30_000),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (res) => {
        this.mensaje = res;
        this.cargarUsuarios();
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = obtenerMensajeError(err, 'No se pudo eliminar el usuario.');
        this.cdr.detectChanges();
      }
    });
  }
}
