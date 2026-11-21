import { ChangeDetectorRef, Component, DestroyRef } from '@angular/core';
import { Router } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize, timeout } from 'rxjs';
import { AuthService } from '../../services/authservice';
import { RegistroRequest } from '../../models/authmodel';
import { obtenerMensajeError } from '../../utils/errorhelper';

@Component({
  selector: 'app-registro',
  templateUrl: './registrocomponent.html',
  styleUrls: ['./registrocomponent.css'],
  standalone: false
})
export class RegistroComponent {
  userData = {
    nombreCompleto: '',
    email: '',
    password: ''
  };
  confirmPassword: string = '';
  errorMessage: string = '';
  successMessage: string = '';
  isLoading: boolean = false;

  constructor(
    private router: Router,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
    private destroyRef: DestroyRef
  ) {}

  onRegister(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.userData.nombreCompleto || !this.userData.email || !this.userData.password || !this.confirmPassword) {
      this.errorMessage = 'Por favor completa todos los campos.';
      return;
    }

    if (this.userData.password.length < 8 || this.userData.password.length > 72) {
      this.errorMessage = 'La contraseña debe tener entre 8 y 72 caracteres.';
      return;
    }

    if (this.userData.password !== this.confirmPassword) {
      this.errorMessage = 'Las contraseñas no coinciden.';
      return;
    }

    const request: RegistroRequest = {
      nombre: this.userData.nombreCompleto,
      email: this.userData.email,
      contrasena: this.userData.password
    };

    this.isLoading = true;

    this.authService.registro(request).pipe(
      timeout(30_000),
      finalize(() => {
        this.isLoading = false;
        if (!this.destroyRef.destroyed) this.cdr.detectChanges();
      }),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: () => {
        this.successMessage = 'Registro exitoso. Revisa tu correo electrónico para verificar tu cuenta.';
        this.userData = {
          nombreCompleto: '',
          email: '',
          password: ''
        };
        this.confirmPassword = '';
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.errorMessage = obtenerMensajeError(
          err,
          'Error al conectar con el servidor. Inténtalo más tarde.'
        );
        this.cdr.detectChanges();
      }
    });
  }
}
