import { ChangeDetectorRef, Component, DestroyRef, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { distinctUntilChanged, finalize, map } from 'rxjs';
import { AuthService } from '../../services/authservice';
import { VerificacionResponse } from '../../models/authmodel';
import { obtenerCodigoError, obtenerMensajeError } from '../../utils/errorhelper';

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
  codigoError: string = '';
  token: string = '';
  emailReenvio: string = '';
  enviandoReenvio: boolean = false;
  reenvioExitoso: boolean = false;
  errorReenvio: string = '';

  constructor(
    private route: ActivatedRoute,
    private authService: AuthService,
    private destroyRef: DestroyRef,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.route.queryParamMap.pipe(
      map((params) => params.get('token')?.trim() ?? ''),
      distinctUntilChanged(),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe((token) => {
      this.token = token;
      this.exito = false;
      this.error = false;
      this.codigoError = '';

      if (this.token) {
        this.verificarTokenBackend(this.token);
      } else {
        this.cargando = false;
        this.error = true;
        this.mensaje = 'No se encontró un token de verificación válido en el enlace.';
      }
    });
  }

  private verificarTokenBackend(token: string): void {
    this.cargando = true;
    this.mensaje = '';

    this.authService.verificarEmail(token).pipe(
      finalize(() => {
        this.cargando = false;
        if (!this.destroyRef.destroyed) {
          this.cdr.detectChanges();
        }
      }),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (res: VerificacionResponse) => {
        this.cargando = false;
        this.error = false;
        this.exito = true;
        this.mensaje = res.mensaje || '¡Tu correo ha sido verificado con éxito!';
        this.cdr.detectChanges();
      },
      error: (err: unknown) => {
        this.cargando = false;
        this.exito = false;
        this.error = true;
        this.mensaje = obtenerMensajeError(
          err,
          'No se pudo verificar el correo. El enlace puede haber expirado o no ser válido.'
        );
        this.codigoError = obtenerCodigoError(err);
        this.cdr.detectChanges();
      },
    });
  }
  reenviarVerificacion(): void {
    if (!this.emailReenvio.trim()) {
      this.errorReenvio = 'Escribe tu correo electrónico.';
      return;
    }
    this.enviandoReenvio = true;
    this.errorReenvio = '';

    this.authService.reenviarVerificacion({ email: this.emailReenvio.trim() }).subscribe({
      next: () => {
        this.enviandoReenvio = false;
        this.reenvioExitoso = true;
      },
      error: () => {
        this.enviandoReenvio = false;
        this.errorReenvio = 'No se pudo enviar el correo. Intenta de nuevo.';
      }
    });
  }

}
