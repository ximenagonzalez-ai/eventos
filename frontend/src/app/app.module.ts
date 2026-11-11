import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';

import { AppComponent } from './app.component';
import { NavbarComponent } from './components/navbarcomponent/navbarcomponent';
import { FooterComponent } from './components/footercomponent/footercomponent';
import { LoginComponent } from './components/logincomponent/logincomponent';
import { RegistroComponent } from './components/registrocomponent/registrocomponent';
import { VerificacionComponent } from './components/verificacioncomponent/verificacioncomponent';
import { routes } from './app.routes';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    FooterComponent,
    LoginComponent,
    RegistroComponent,
    VerificacionComponent,
  ],
  imports: [BrowserModule, FormsModule, HttpClientModule, RouterModule.forRoot(routes)],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
