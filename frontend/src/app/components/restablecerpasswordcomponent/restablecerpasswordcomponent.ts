import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/authservice';

@Component({
  selector: 'app-restablecer-password',
  templateUrl: './restablecerpasswordcomponent.html',
  styleUrls: ['./restablecerpasswordcomponent.css'],
  standalone: false
})
export class RestablecerPasswordComponent implements OnInit {
  token: string = '';
  nuevaContrasena: string = '';
  confirmarContrasena: string = '';
  mensaje: string = '';
  error: string = '';
  isLoading: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParamMap.get('token') || '';
  }

  onSubmit(): void {
    if (this.nuevaContrasena.length < 8) {
      this.error = 'La contraseña debe tener al menos 8 caracteres.';
      return;
    }
    if (this.nuevaContrasena !== this.confirmarContrasena) {
      this.error = 'Las contraseñas no coinciden.';
      return;
    }

    this.isLoading = true;
    this.mensaje = '';
    this.error = '';

    this.authService.restablecerPassword({
      token: this.token,
      nuevaContrasena: this.nuevaContrasena
    }).subscribe({
      next: (res) => {
        this.mensaje = res.mensaje;
        this.isLoading = false;
        setTimeout(() => this.router.navigate(['/login']), 2500);
      },
      error: (err) => {
        this.error = err.error?.mensaje || 'No se pudo restablecer la contraseña.';
        this.isLoading = false;
      }
    });
  }
}
