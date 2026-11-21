import { NgModule, provideZoneChangeDetection } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { RouterModule } from '@angular/router';

import { AppComponent } from './app.component';
import { NavbarComponent } from './components/navbarcomponent/navbarcomponent';
import { FooterComponent } from './components/footercomponent/footercomponent';
import { LoginComponent } from './components/logincomponent/logincomponent';
import { RegistroComponent } from './components/registrocomponent/registrocomponent';
import { VerificacionComponent } from './components/verificacioncomponent/verificacioncomponent';
import { MiBoletaComponent } from './components/miboletacomponent/miboletacomponent';
import { ItinerarioComponent } from './components/itinerariocomponent/itinerariocomponent';
import { AuditoriaComponent } from './components/auditoriacomponent/auditoriacomponent';
import { OlvidePasswordComponent } from './components/olvidepasswordcomponent/olvidepasswordcomponent';
import { RestablecerPasswordComponent } from './components/restablecerpasswordcomponent/restablecerpasswordcomponent';
import { PerfilComponent } from './components/perfilcomponent/perfilcomponent';
import { CatalogoComponent } from './components/catalogocomponent/catalogocomponent';
import { EventoDetalleComponent } from './components/eventodetallecomponent/eventodetallecomponent';
import { RutaComponent } from './components/rutacomponent/rutacomponent';
import { AyudaComponent } from './components/ayudacomponent/ayudacomponent';
import { AdministracionComponent } from './components/administracioncomponent/administracioncomponent';
import { AjustesComponent } from './components/ajustescomponent/ajustescomponent';
import { routes } from './app.routes';
import { AuthInterceptor } from './interceptors/authinterceptor';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    FooterComponent,
    LoginComponent,
    RegistroComponent,
    VerificacionComponent,
    MiBoletaComponent,
    ItinerarioComponent,
    AuditoriaComponent,
    OlvidePasswordComponent,
    RestablecerPasswordComponent,
    PerfilComponent,
    CatalogoComponent,
    EventoDetalleComponent,
    RutaComponent,
    AyudaComponent,
    AdministracionComponent,
    AjustesComponent,
  ],
  imports: [
    BrowserModule,
    CommonModule,
    FormsModule,
    HttpClientModule,
    RouterModule.forRoot(routes),
  ],
  providers: [
    provideZoneChangeDetection(),
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
