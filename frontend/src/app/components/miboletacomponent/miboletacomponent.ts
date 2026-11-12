import { Component, OnInit } from '@angular/core';
import { Boleta } from '../../models/boletamodel';

@Component({
  selector: 'app-miboletacomponent',
  standalone: false,
  templateUrl: './miboletacomponent.html',
  styleUrl: './miboletacomponent.css',
})
export class MiBoletaComponent implements OnInit {
  mostrarModal: boolean = false;
  esAdmin: boolean = true;

  boletas: Boleta[] = [
    {
      id: 1,
      codigo: 'BOL-109283',
      eventoNombre: 'Concierto en Vivo',
      tipoBoleta: 'VIP',
      precio: 250000,
      fechaEmision: '2026-11-10',
      estado: 'ACTIVA',
    },
  ];

  nuevaBoleta: Boleta = {
    codigo: '',
    eventoNombre: '',
    tipoBoleta: 'General',
    precio: 0,
    fechaEmision: new Date().toISOString().split('T')[0],
    estado: 'ACTIVA',
  };

  ngOnInit(): void {}

  abrirModal(): void {
    this.mostrarModal = true;
  }

  cerrarModal(): void {
    this.mostrarModal = false;
    this.limpiarFormulario();
  }

  generarCodigo(): string {
    return 'BOL-' + Math.floor(100000 + Math.random() * 900000);
  }

  crearBoleta(): void {
    if (!this.nuevaBoleta.eventoNombre || this.nuevaBoleta.precio <= 0) {
      alert('Por favor complete el nombre del evento y un precio válido.');
      return;
    }

    const boletaAEmitir: Boleta = {
      ...this.nuevaBoleta,
      id: this.boletas.length + 1,
      codigo: this.generarCodigo(),
    };

    this.boletas.push(boletaAEmitir);
    this.cerrarModal();
  }

  limpiarFormulario(): void {
    this.nuevaBoleta = {
      codigo: '',
      eventoNombre: '',
      tipoBoleta: 'General',
      precio: 0,
      fechaEmision: new Date().toISOString().split('T')[0],
      estado: 'ACTIVA',
    };
  }

  cancelarBoleta(id?: number): void {
    const boleta = this.boletas.find((b) => b.id === id);
    if (boleta) {
      boleta.estado = 'CANCELADA';
    }
  }
}
