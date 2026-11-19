import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { EventoService } from '../../services/eventoservice';
import { Evento } from '../../models/eventomodel';
import { obtenerMensajeError } from '../../utils/errorhelper';

@Component({
  selector: 'app-ruta',
  templateUrl: './rutacomponent.html',
  styleUrls: ['./rutacomponent.css'],
  standalone: false
})
export class RutaComponent implements OnInit {

  evento: Evento | null = null;
  isLoading: boolean = true;
  error: string = '';

  origenLat: number | null = null;
  origenLng: number | null = null;
  obteniendoUbicacion: boolean = false;
  errorUbicacion: string = '';

  instrucciones: string[] = [];
  distanciaTotal: string = '';
  duracionTotal: string = '';
  calculandoRuta: boolean = false;
  errorRuta: string = '';

  mapaUrl: SafeResourceUrl | null = null;

  constructor(
    private route: ActivatedRoute,
    private eventoService: EventoService,
    private http: HttpClient,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.error = '';
      this.eventoService.obtenerPorId(id).subscribe({
        next: (data) => {
          this.evento = data;
          this.isLoading = false;
          if (data.lugar?.latitud && data.lugar?.longitud) {
            this.mapaUrl = this.construirMapaUrl(data.lugar.latitud, data.lugar.longitud);
          }
        },
        error: (err) => {
          this.error = obtenerMensajeError(err, 'No se encontró el evento.');
          this.isLoading = false;
        }
      });
    } else {
      this.error = 'El identificador del evento no es válido.';
      this.isLoading = false;
    }
  }

  obtenerMiUbicacion(): void {
    if (!navigator.geolocation) {
      this.errorUbicacion = 'Tu navegador no soporta geolocalización.';
      return;
    }

    this.obteniendoUbicacion = true;
    this.errorUbicacion = '';
    this.errorRuta = '';
    this.instrucciones = [];

    navigator.geolocation.getCurrentPosition(
      (posicion) => {
        this.origenLat = posicion.coords.latitude;
        this.origenLng = posicion.coords.longitude;
        this.obteniendoUbicacion = false;
        this.calcularRuta();
      },
      (error) => {
        this.obteniendoUbicacion = false;
        if (error.code === 1) {
          this.errorUbicacion = 'Permiso de ubicación denegado. Actívalo en tu navegador.';
        } else {
          this.errorUbicacion = 'No se pudo obtener tu ubicación. Intenta de nuevo.';
        }
      },
      { timeout: 10000 }
    );
  }

  calcularRuta(): void {
    if (!this.origenLat || !this.origenLng) {
      this.errorRuta = 'Primero obtén tu ubicación.';
      return;
    }
    if (!this.evento?.lugar?.latitud || !this.evento?.lugar?.longitud) {
      this.errorRuta = 'El evento no tiene coordenadas registradas.';
      return;
    }

    this.calculandoRuta = true;
    this.errorRuta = '';
    this.instrucciones = [];

    const token = localStorage.getItem('token') || '';
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    const body = {
      origenLat: this.origenLat,
      origenLng: this.origenLng,
      destinoLat: this.evento.lugar.latitud,
      destinoLng: this.evento.lugar.longitud
    };

    this.http.post<any>('http://localhost:8080/api/ruta/calcular', body, { headers })
      .subscribe({
        next: (respuesta) => {
          this.calculandoRuta = false;
          this.instrucciones = respuesta.instrucciones || [];
          this.distanciaTotal = respuesta.distanciaTotal || '';
          this.duracionTotal = respuesta.duracionTotal || '';

          this.mapaUrl = this.construirMapaUrlConOrigen(
            this.origenLat!, this.origenLng!,
            this.evento!.lugar!.latitud!, this.evento!.lugar!.longitud!
          );
        },
        error: (err) => {
          this.calculandoRuta = false;
          this.errorRuta = obtenerMensajeError(
            err,
            'No se pudo calcular la ruta. Verifica tu sesión e intenta de nuevo.'
          );
        }
      });
  }

  abrirEnGoogleMaps(): void {
    if (!this.evento?.lugar) return;
    const { latitud, longitud } = this.evento.lugar;

    let url: string;
    if (this.origenLat && this.origenLng) {
      url = `https://www.google.com/maps/dir/${this.origenLat},${this.origenLng}/${latitud},${longitud}`;
    } else {
      url = `https://www.google.com/maps/search/?api=1&query=${latitud},${longitud}`;
    }
    window.open(url, '_blank');
  }

  private construirMapaUrl(lat: number, lng: number): SafeResourceUrl {
    const url = `https://www.openstreetmap.org/export/embed.html` +
      `?bbox=${lng - 0.01},${lat - 0.01},${lng + 0.01},${lat + 0.01}` +
      `&layer=mapnik&marker=${lat},${lng}`;
    return this.sanitizer.bypassSecurityTrustResourceUrl(url);
  }

  private construirMapaUrlConOrigen(
    oLat: number, oLng: number,
    dLat: number, dLng: number
  ): SafeResourceUrl {
    const minLat = Math.min(oLat, dLat) - 0.02;
    const maxLat = Math.max(oLat, dLat) + 0.02;
    const minLng = Math.min(oLng, dLng) - 0.02;
    const maxLng = Math.max(oLng, dLng) + 0.02;
    const url = `https://www.openstreetmap.org/export/embed.html` +
      `?bbox=${minLng},${minLat},${maxLng},${maxLat}` +
      `&layer=mapnik&marker=${dLat},${dLng}`;
    return this.sanitizer.bypassSecurityTrustResourceUrl(url);
  }

  get iconoPaso(): string[] {
    return this.instrucciones.map(ins => {
      const i = ins.toLowerCase();
      if (i.includes('derecha'))  return '➡️';
      if (i.includes('izquierda')) return '⬅️';
      if (i.includes('continúa') || i.includes('continua')) return '⬆️';
      if (i.includes('destino') || i.includes('llegada'))   return '🏁';
      if (i.includes('rotonda') || i.includes('redonda'))   return '🔄';
      if (i.includes('gira'))     return '↪️';
      return '📍';
    });
  }
}
