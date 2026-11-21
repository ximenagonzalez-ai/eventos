import { ChangeDetectorRef, Component, DestroyRef, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize, timeout } from 'rxjs';
import { AuthService } from '../../services/authservice';
import { ThemeService } from '../../services/themeservice';
import { obtenerMensajeError } from '../../utils/errorhelper';

@Component({
  selector: 'app-ajustes',
  templateUrl: './ajustescomponent.html',
  styleUrls: ['./ajustescomponent.css'],
  standalone: false
})
export class AjustesComponent implements OnInit {

  // ── Cambio de contraseña ──────────────────────────────────────────
  contrasenaActual = '';
  nuevaContrasena = '';
  confirmarContrasena = '';
  mostrarContrasenas = false;
  guardandoContrasena = false;
  mensajeContrasena = '';
  errorContrasena = '';

  // ── Notificaciones ───────────────────────────────────────────────
  notifEventosNuevos = true;
  notifRecordatorios = true;
  notifActualizaciones = true;
  guardandoNotif = false;
  mensajeNotif = '';

  // ── Apariencia ────────────────────────────────────────────────────
  temaSeleccionado = 'dark';

  temas = [
    { id: 'dark',    etiqueta: 'Oscuro',    emoji: '🌙' },
    { id: 'light',   etiqueta: 'Claro',     emoji: '☀️' },
    { id: 'emerald', etiqueta: 'Esmeralda', emoji: '💚' },
    { id: 'purple',  etiqueta: 'Morado',    emoji: '💜' },
    { id: 'sunset',  etiqueta: 'Atardecer', emoji: '🌅' },
    { id: 'pink',    etiqueta: 'Rosa Neón', emoji: '🩷' },
    { id: 'wine',    etiqueta: 'Rojo Vino', emoji: '🍷' },
    { id: 'gold',    etiqueta: 'Dorado',    emoji: '✨' },
  ];

  constructor(
    private authService: AuthService,
    private themeService: ThemeService,
    private destroyRef: DestroyRef,
    private cdr: ChangeDetectorRef
  ) {}

  get email(): string {
    return localStorage.getItem('email') ?? '';
  }

  ngOnInit(): void {
    this.temaSeleccionado = this.themeService.getTheme();
    this.notifEventosNuevos   = localStorage.getItem('notif_nuevos')          !== 'false';
    this.notifRecordatorios   = localStorage.getItem('notif_recordatorios')   !== 'false';
    this.notifActualizaciones = localStorage.getItem('notif_actualizaciones') !== 'false';
  }

  cambiarContrasena(): void {
    this.mensajeContrasena = '';
    this.errorContrasena = '';

    if (!this.contrasenaActual) {
      this.errorContrasena = 'Ingresa tu contraseña actual.';
      return;
    }
    if (this.nuevaContrasena.length < 8) {
      this.errorContrasena = 'La nueva contraseña debe tener al menos 8 caracteres.';
      return;
    }
    if (this.nuevaContrasena !== this.confirmarContrasena) {
      this.errorContrasena = 'Las contraseñas no coinciden.';
      return;
    }
    if (this.contrasenaActual === this.nuevaContrasena) {
      this.errorContrasena = 'La nueva contraseña debe ser diferente a la actual.';
      return;
    }

    this.guardandoContrasena = true;
    this.authService.olvidePassword({ email: this.email }).pipe(
      timeout(30_000),
      finalize(() => {
        this.guardandoContrasena = false;
        this.cdr.detectChanges();
      }),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: () => {
        this.mensajeContrasena =
          'Te enviamos un enlace a tu correo para confirmar el cambio de contraseña.';
        this.contrasenaActual = '';
        this.nuevaContrasena = '';
        this.confirmarContrasena = '';
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.errorContrasena = obtenerMensajeError(err, 'No se pudo procesar la solicitud.');
        this.cdr.detectChanges();
      }
    });
  }

  guardarNotificaciones(): void {
    this.guardandoNotif = true;
    this.mensajeNotif = '';

    localStorage.setItem('notif_nuevos',         String(this.notifEventosNuevos));
    localStorage.setItem('notif_recordatorios',   String(this.notifRecordatorios));
    localStorage.setItem('notif_actualizaciones', String(this.notifActualizaciones));

    setTimeout(() => {
      this.guardandoNotif = false;
      this.mensajeNotif = 'Preferencias guardadas.';
      this.cdr.detectChanges();
    }, 500);
  }

  seleccionarTema(id: string): void {
    this.temaSeleccionado = id;
    this.themeService.setTheme(id);
  }
}
