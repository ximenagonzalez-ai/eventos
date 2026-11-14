import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '../../services/authservice';
import { VerificacionResponse } from '../../models/authmodel';

@Component({
  selector: 'app-verificacioncomponent',
  standalone: false,
  templateUrl: './verificacioncomponent.html',
  styleUrls: ['./verificacioncomponent.css'],
})
export class VerificacionComponent implements OnInit {
  cargando: boolean = true;
  exito: boolean = false;
  error: boolean = false;
  mensaje: string = '';
  token: string = '';

  constructor(
    private route: ActivatedRoute,
    private authService: AuthService
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
      next: (res: VerificacionResponse) => {
        this.cargando = false;
        this.exito = true;
        this.mensaje = res.mensaje || '¡Tu correo ha sido verificado con éxito!';
      },
      error: () => {
        this.cargando = false;
        this.error = true;
        this.mensaje = 'El enlace de verificación ha expirado o no es válido.';
      },
    });
  }
}
