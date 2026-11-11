import { Routes } from '@angular/router';
import { LoginComponent } from './components/logincomponent/logincomponent';
import { RegistroComponent } from './components/registrocomponent/registrocomponent';
import { VerificacionComponent } from './components/verificacioncomponent/verificacioncomponent';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'registro', component: RegistroComponent },
  { path: 'verificacion', component: VerificacionComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
];
