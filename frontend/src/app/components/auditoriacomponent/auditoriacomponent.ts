import { Component, OnInit } from '@angular/core';
import { AuditoriaService } from '../../services/auditoriaservice';
import { Auditoria } from '../../models/auditoriamodel';

@Component({
  selector: 'app-auditoria',
  templateUrl: './auditoriacomponent.html',
  styleUrls: ['./auditoriacomponent.css'],
  standalone: false
})
export class AuditoriaComponent implements OnInit {
  auditorias: Auditoria[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';

  constructor(private auditoriaService: AuditoriaService) {}

  ngOnInit(): void {
    this.cargarAuditorias();
  }

  cargarAuditorias(): void {
    this.isLoading = true;
    const usuarioIdEjemplo = 1;

    this.auditoriaService.obtenerAuditoriasPorUsuario(usuarioIdEjemplo).subscribe({
      next: (data) => {
        this.auditorias = data;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'No se pudieron cargar los registros de auditoría.';
        this.isLoading = false;
      }
    });
  }
}
