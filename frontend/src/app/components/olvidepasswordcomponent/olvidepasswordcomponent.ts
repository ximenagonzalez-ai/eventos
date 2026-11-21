import { ChangeDetectorRef, Component, DestroyRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize, timeout } from 'rxjs';
import { AuthService } from '../../services/authservice';
import { obtenerMensajeError } from '../../utils/errorhelper';

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

  constructor(
    private authService: AuthService,
    private destroyRef: DestroyRef,
    private cdr: ChangeDetectorRef
  ) {}

  onSubmit(): void {
    if (!this.email.trim()) {
      this.error = 'Ingresa tu correo electrónico.';
      return;
    }
    this.isLoading = true;
    this.mensaje = '';
    this.error = '';

    this.authService.olvidePassword({ email: this.email }).pipe(
      timeout(30_000),
      finalize(() => {
        this.isLoading = false;
        if (!this.destroyRef.destroyed) this.cdr.detectChanges();
      }),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (res) => {
        this.mensaje = res.mensaje;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = obtenerMensajeError(err, 'Ocurrió un error al procesar la solicitud.');
        this.cdr.detectChanges();
      }
    });
  }
}
