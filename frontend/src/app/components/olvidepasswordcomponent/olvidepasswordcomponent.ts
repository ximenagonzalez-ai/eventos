import { Component } from '@angular/core';
import { AuthService } from '../../services/authservice';

@Component({
  selector: 'app-olvide-password',
  templateUrl: './olvidepasswordcomponent.html',
  styleUrls: ['./olvidepasswordcomponent.css'],
  standalone: false
})
export class OlvidePasswordComponent {
  email: string = '';
  mensaje: string = '';
  error: string = '';
  isLoading: boolean = false;

  constructor(private authService: AuthService) {}

  onSubmit(): void {
    if (!this.email.trim()) return;
    this.isLoading = true;
    this.mensaje = '';
    this.error = '';

    this.authService.olvidePassword({ email: this.email }).subscribe({
      next: (res) => {
        this.mensaje = res.mensaje;
        this.isLoading = false;
      },
      error: (err) => {
        this.error = err.error?.mensaje || 'Ocurrió un error al procesar la solicitud.';
        this.isLoading = false;
      }
    });
  }
}
