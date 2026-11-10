import { Component } from '@angular/core';

@Component({
  selector: 'app-footer',
  standalone: false,
  templateUrl: './footercomponent.html',
  styleUrl: './footercomponent.css'
})
export class FooterComponent {
  anioActual: number = new Date().getFullYear();
}
