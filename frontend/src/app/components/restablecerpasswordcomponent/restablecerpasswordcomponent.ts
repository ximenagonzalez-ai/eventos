import { ChangeDetectorRef, Component, DestroyRef, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize, timeout } from 'rxjs';
import { AuthService } from '../../services/authservice';
import { obtenerMensajeError } from '../../utils/errorhelper';

@Component({
  selector: 'app-restablecer-password',
  templateUrl: './restablecerpasswordcomponent.html',
  styleUrls: ['./restablecerpasswordcomponent.css'],
  standalone: false
})
export class RestablecerPasswordComponent implements OnInit {
  token: string = '';
  nuevaContrasena: string = '';
  confirmarContrasena: string = '';
  mensaje: string = '';
  error: string = '';
  isLoading: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private destroyRef: DestroyRef,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParamMap.get('token') || '';
  }

  onSubmit(): void {
    if (this.nuevaContrasena.length < 8) {
      this.error = 'La contraseña debe tener al menos 8 caracteres.';
      return;
    }
    if (this.nuevaContrasena !== this.confirmarContrasena) {
      this.error = 'Las contraseñas no coinciden.';
      return;
    }

    this.isLoading = true;
    this.mensaje = '';
    this.error = '';

    this.authService.restablecerPassword({
      token: this.token,
      nuevaContrasena: this.nuevaContrasena
    }).pipe(
      timeout(30_000),
      finalize(() => {
        this.isLoading = false;
        if (!this.destroyRef.destroyed) this.cdr.detectChanges();
      }),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (res) => {
        this.mensaje = res.mensaje || 'Contraseña restablecida correctamente.';
        this.cdr.detectChanges();
        setTimeout(() => this.router.navigate(['/login']), 2500);
      },
      error: (err) => {
        this.error = obtenerMensajeError(
          err,
          'No se pudo restablecer la contraseña.'
        );
        this.cdr.detectChanges();
      }
    });
  }
}
