import { Routes } from '@angular/router';
import { CatalogoComponent } from './components/catalogocomponent/catalogocomponent';
import { EventoDetalleComponent } from './components/eventodetallecomponent/eventodetallecomponent';
import { RutaComponent } from './components/rutacomponent/rutacomponent';
import { LoginComponent } from './components/logincomponent/logincomponent';
import { RegistroComponent } from './components/registrocomponent/registrocomponent';
import { VerificacionComponent } from './components/verificacioncomponent/verificacioncomponent';
import { MiBoletaComponent } from './components/miboletacomponent/miboletacomponent';
import { ItinerarioComponent } from './components/itinerariocomponent/itinerariocomponent';
import { AuditoriaComponent } from './components/auditoriacomponent/auditoriacomponent';
import { OlvidePasswordComponent } from './components/olvidepasswordcomponent/olvidepasswordcomponent';
import { RestablecerPasswordComponent } from './components/restablecerpasswordcomponent/restablecerpasswordcomponent';
import { PerfilComponent } from './components/perfilcomponent/perfilcomponent';
import { AyudaComponent } from './components/ayudacomponent/ayudacomponent';
import { AdministracionComponent } from './components/administracioncomponent/administracioncomponent';
import { adminGuard, authGuard } from './guards/role.guards';

export const routes: Routes = [
  { path: '', component: CatalogoComponent },
  { path: 'evento/:id', component: EventoDetalleComponent },
  { path: 'evento/:id/ruta', component: RutaComponent },
  { path: 'login', component: LoginComponent },
  { path: 'registro', component: RegistroComponent },
  { path: 'verificacion', component: VerificacionComponent },
  { path: 'verificar-email', component: VerificacionComponent },
  { path: 'mis-boletas', component: MiBoletaComponent, canActivate: [authGuard] },
  { path: 'itinerario', component: ItinerarioComponent, canActivate: [authGuard] },
  { path: 'auditoria', component: AuditoriaComponent, canActivate: [adminGuard] },
  { path: 'administracion', component: AdministracionComponent, canActivate: [adminGuard] },
  { path: 'olvide-password', component: OlvidePasswordComponent },
  { path: 'restablecer-password', component: RestablecerPasswordComponent },
  { path: 'perfil', component: PerfilComponent, canActivate: [authGuard] },
  { path: 'ayuda', component: AyudaComponent },
  { path: '**', redirectTo: '' },
];
