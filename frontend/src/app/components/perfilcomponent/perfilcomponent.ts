import { Component, OnInit } from '@angular/core';
import { UsuarioService } from '../../services/userservice';
import { Usuario } from '../../models/usermodel';

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

  constructor(private usuarioService: UsuarioService) {}

  get isAdmin(): boolean {
    return localStorage.getItem('rol') === 'ADMIN';
  }

  ngOnInit(): void {
    this.cargarUsuarios();
  }

  cargarUsuarios(): void {
    this.usuarioService.obtenerTodos().subscribe({
      next: (data) => {
        this.usuarios = data || [];
        const emailActual = localStorage.getItem('email');
        if (emailActual) {
          this.usuarioSeleccionado = this.usuarios.find(u => u.email === emailActual) || this.usuarios[0] || null;
        } else {
          this.usuarioSeleccionado = this.usuarios[0] || null;
        }
      },
      error: () => {
        this.error = 'No se pudo cargar la información del usuario.';
      }
    });
  }

  eliminarUsuario(id?: number): void {
    if (!id || !confirm('¿Seguro que deseas eliminar este usuario?')) return;
    this.usuarioService.eliminarPorId(id).subscribe({
      next: (res) => {
        this.mensaje = res;
        this.cargarUsuarios();
      },
      error: () => {
        this.error = 'No se pudo eliminar el usuario.';
      }
    });
  }
}
