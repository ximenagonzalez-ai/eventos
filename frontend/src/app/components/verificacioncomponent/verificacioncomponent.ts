import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '../../services/authservice';

@Component({
  selector: 'app-verificacioncomponent',
  standalone: false,
  templateUrl: './verificacioncomponent.html',
  styleUrl: './verificacioncomponent.css',
})
export class VerificacionComponent implements OnInit {
  cargando: boolean = true;
  exito: boolean = false;
  error: boolean = false;
  mensaje: string = '';
  token: string = '';

  constructor(
    private route: ActivatedRoute,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      this.token = params['token'];
      if (this.token) {
        this.verificarTokenBackend(this.token);
      } else {
        this.cargando = false;
        this.error = true;
        this.mensaje = 'No se encontró un token de verificación válido en el enlace.';
      }
    });
  }

  verificarTokenBackend(token: string): void {
    this.authService.verificarEmail(token).subscribe({
      next: (res: any) => {
        this.cargando = false;
        this.exito = true;
        this.mensaje = res.message || '¡Tu correo ha sido verificado con éxito!';
      },
      error: () => {
        this.cargando = false;
        this.error = true;
        this.mensaje = 'El enlace de verificación ha expirado o no es válido.';
      },
    });
  }

  reenviarCorreo(): void {
    this.authService.reenviarVerificacion(this.token).subscribe({
      next: () => {
        alert('Se ha enviado un nuevo enlace de verificación a tu correo.');
      },
      error: () => {
        alert('Hubo un error al intentar reenviar el correo.');
      },
    });
  }
}
