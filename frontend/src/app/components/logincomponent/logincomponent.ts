import { ChangeDetectorRef, Component, DestroyRef } from '@angular/core';
import { Router } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize, timeout } from 'rxjs';
import { AuthService } from '../../services/authservice';
import { LoginRequest } from '../../models/authmodel';
import { obtenerMensajeError } from '../../utils/errorhelper';

@Component({
  selector: 'app-login',
  templateUrl: './logincomponent.html',
  styleUrls: ['./logincomponent.css'],
  standalone: false
})
export class LoginComponent {
  credentials = {
    email: '',
    password: ''
  };

  errorMessage: string = '';
  isLoading: boolean = false;

  constructor(
    private router: Router,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
    private destroyRef: DestroyRef
  ) {}

  onLogin(): void {
    this.errorMessage = '';

    if (!this.credentials.email || !this.credentials.password) {
      this.errorMessage = 'Por favor completa todos los campos.';
      return;
    }

    const request: LoginRequest = {
      email: this.credentials.email,
      contrasena: this.credentials.password
    };

    this.isLoading = true;

    this.authService.login(request).pipe(
      timeout(30_000),
      finalize(() => {
        this.isLoading = false;
        if (!this.destroyRef.destroyed) this.cdr.detectChanges();
      }),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (res: any) => {
        const token = res?.token || res?.jwt || res?.accessToken;
        const rol = res?.rol || res?.role || res?.usuario?.rol;
        const usuarioId = res?.id || res?.usuarioId || res?.usuario?.id;
        const email = res?.email || res?.usuario?.email || this.credentials.email;

        if (token) {
          localStorage.setItem('token', token);
        }
        if (rol) {
          localStorage.setItem('rol', rol);
        }
        if (usuarioId) {
          localStorage.setItem('usuarioId', String(usuarioId));
        }
        if (email) {
          localStorage.setItem('email', email);
        }

        this.cdr.detectChanges();
        this.router.navigate(['/mis-boletas']);
      },
      error: (err) => {
        this.errorMessage = obtenerMensajeError(
          err,
          'Correo o contraseña incorrectos, o cuenta sin verificar.'
        );
        this.cdr.detectChanges();
      }
    });
  }
}
