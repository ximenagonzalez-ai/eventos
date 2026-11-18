import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-ayuda',
  standalone: false,
  templateUrl: './ayudacomponent.html',
  styleUrl: './ayudacomponent.css'
})
export class AyudaComponent implements OnInit {
  faqs = [
    {
      pregunta: '¿Cómo compro boletas en VibePass?',
      respuesta: 'Explora cualquier evento en la sección de Conciertos, Deportes o Teatro, haz clic en "Ver Boletas y Detalles", elige la localidad y confirma tu compra.'
    },
    {
      pregunta: '¿Dónde veo las boletas que ya compré?',
      respuesta: 'Una vez inicies sesión, entra a "Mis Boletas" en la barra superior o en el menú de "Mi Cuenta" para consultar tus entradas y códigos.'
    },
    {
      pregunta: '¿Qué hago si olvidé mi contraseña?',
      respuesta: 'Ve a "Iniciar Sesión", haz clic en "¿Olvidaste tu contraseña?", ingresa tu correo y sigue el enlace de recuperación que llegará a tu bandeja.'
    },
    {
      pregunta: '¿Puedo cancelar o anular una boleta?',
      respuesta: 'Las anulaciones y cambios de estado son gestionados por el equipo administrador según las políticas del organizador del evento.'
    }
  ];

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.route.fragment.subscribe(fragment => {
      if (fragment) {
        setTimeout(() => {
          const el = document.getElementById(fragment);
          if (el) el.scrollIntoView({ behavior: 'smooth' });
        }, 100);
      }
    });
  }
}
