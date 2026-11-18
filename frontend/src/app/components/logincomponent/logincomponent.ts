import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/authservice';
import { LoginRequest } from '../../models/authmodel';

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

  constructor(private router: Router, private authService: AuthService) {}

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

    this.authService.login(request).subscribe({
      next: () => {
        this.isLoading = false;
        this.router.navigate(['/mis-boletas']);
      },
      error: (err) => {
        this.isLoading = false;
        if (err.status === 401 || err.status === 403) {
          this.errorMessage = 'Correo o contraseña incorrectos, o cuenta sin verificar.';
        } else {
          this.errorMessage = 'Error al conectar con el servidor. Inténtalo más tarde.';
        }
      }
    });
  }
}
