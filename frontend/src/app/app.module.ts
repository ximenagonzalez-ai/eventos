import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { AppComponent } from './app.component';
import { NavbarComponent } from './components/navbarcomponent/navbarcomponent';
import { FooterComponent } from './components/footercomponent/footercomponent';
import { LoginComponent } from './components/logincomponent/logincomponent';
import { routes } from './app.routes';
import {RegistroComponent} from './components/registrocomponent/registrocomponent';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    FooterComponent,
    LoginComponent,
    RegistroComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    RouterModule.forRoot(routes)
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
