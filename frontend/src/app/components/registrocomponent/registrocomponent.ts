import { Component } from '@angular/core';
import { Router } from '@angular/router';

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
    rol: 'CLIENTE'
  };
  confirmPassword: string = '';
  errorMessage: string = '';

  constructor(private router: Router) {}

  onRegister(): void {
    if (!this.userData.nombreCompleto || !this.userData.email || !this.userData.password || !this.confirmPassword) {
      this.errorMessage = 'Por favor completa todos los campos.';
      return;
    }

    if (this.userData.password !== this.confirmPassword) {
      this.errorMessage = 'Las contraseñas no coinciden.';
      return;
    }

    this.errorMessage = '';
    this.router.navigate(['/login']);
  }
}
