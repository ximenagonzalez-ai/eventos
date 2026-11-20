import { Component, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
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
    password: '',
    rol: 'USER'
  };
  confirmPassword: string = '';
  errorMessage: string = '';
  successMessage: string = '';
  isLoading: boolean = false;

  constructor(
    private router: Router,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  onRegister(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.userData.nombreCompleto || !this.userData.email || !this.userData.password || !this.confirmPassword) {
      this.errorMessage = 'Por favor completa todos los campos.';
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

    this.authService.registro(request).subscribe({
      next: () => {
        this.isLoading = false;
        this.successMessage = 'Registro exitoso. Revisa tu correo electrónico para verificar tu cuenta.';
        this.userData = {
          nombreCompleto: '',
          email: '',
          password: '',
          rol: 'USER'
        };
        this.confirmPassword = '';
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = obtenerMensajeError(
          err,
          'Error al conectar con el servidor. Inténtalo más tarde.'
        );
        this.cdr.detectChanges();
      }
    });
  }
}
