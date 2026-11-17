import { Routes } from '@angular/router';
import { LoginComponent } from './components/logincomponent/logincomponent';
import { RegistroComponent } from './components/registrocomponent/registrocomponent';
import { VerificacionComponent } from './components/verificacioncomponent/verificacioncomponent';
import { MiBoletaComponent } from './components/miboletacomponent/miboletacomponent';
import { AuditoriaComponent } from './components/auditoriacomponent/auditoriacomponent';
import { OlvidePasswordComponent } from './components/olvidepasswordcomponent/olvidepasswordcomponent';
import { RestablecerPasswordComponent } from './components/restablecerpasswordcomponent/restablecerpasswordcomponent';
import { PerfilComponent } from './components/perfilcomponent/perfilcomponent';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'registro', component: RegistroComponent },
  { path: 'verificacion', component: VerificacionComponent },
  { path: 'olvide-password', component: OlvidePasswordComponent },
  { path: 'restablecer-password', component: RestablecerPasswordComponent },
  { path: 'perfil', component: PerfilComponent },
  { path: 'mis-boletas', component: MiBoletaComponent },
  { path: 'auditoria', component: AuditoriaComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
];
