import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';

interface SolicitudPropietario {
  codusu: number;
  nomusu: string;
  apepusu: string;
  apemusu: string;
  docusu: string;
  corusu: string;
  celusu: string;
  fecusu: string;
  estusu: string;
  fecrechazo?: string;
  fecsolicitud?: string;
  diasRestantes: number;
}

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './admin-dashboard.html',
  styleUrls: ['./admin-dashboard.css']
})
export class AdminDashboard implements OnInit {

  pestanaActual: string = 'Pendientes';
  solicitudes: SolicitudPropietario[] = [];          // Alimenta los contadores de las tarjetas de arriba
  solicitudesFiltradas: SolicitudPropietario[] = [];  // Arreglo directo que consumirá tu tabla HTML

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.cargarSolicitudes();
  }

  cambiarPestana(pestana: string) {
    this.pestanaActual = pestana;
    this.filtrarDatos();
  }

  cargarSolicitudes() {
    this.http.get<SolicitudPropietario[]>(
      'http://localhost:8080/usuario/solicitudes'
    ).subscribe({
      next: (data) => {
        this.solicitudes = data;
        console.log('Datos cargados de inmediato:', data);

        // Ejecutamos el filtro inicial y forzamos el renderizado inmediato
        this.filtrarDatos();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al conectar con la API:', err);
      }
    });
  }

  filtrarDatos() {
    // Traducimos el texto del botón al formato exacto que guarda tu Base de Datos en Spring Boot
    let estadoBuscado = this.pestanaActual;
    if (this.pestanaActual === 'Pendientes') estadoBuscado = 'Pendiente';
    if (this.pestanaActual === 'Aprobados') estadoBuscado = 'Aprobado';
    if (this.pestanaActual === 'Rechazados') estadoBuscado = 'Rechazado';

    this.solicitudesFiltradas = this.solicitudes.filter(
      s => s.estusu === estadoBuscado
    );
  }

  aprobarUsuario(id: number) {

    if (!confirm('¿Desea aprobar esta solicitud?')) {
      return;
    }

    this.http.put(
      `http://localhost:8080/usuario/aprobar/${id}`,
      {}
    ).subscribe({

      next: () => {

        alert('Usuario aprobado');

        this.cargarSolicitudes();

      },

      error: (err) => console.error(err)

    });

  }

  rechazarUsuario(id: number) {

    if (!confirm('¿Desea rechazar esta solicitud?')) {
      return;
    }

    this.http.put(
      `http://localhost:8080/usuario/rechazar/${id}`,
      {}
    ).subscribe({

      next: () => {

        alert('Usuario rechazado');

        this.cargarSolicitudes();

      },

      error: (err) => console.error(err)

    });

  }
  eliminarUsuario(id: number) {

    if (!confirm('¿Desea eliminar esta solicitud?')) {
      return;
    }

    this.http.delete(
      `http://localhost:8080/usuario/eliminar/${id}`
    ).subscribe({

      next: () => {

        alert('Solicitud eliminada');

        this.cargarSolicitudes();

      },

      error: (err) => {

        console.error(err);

        alert('Error al eliminar');

      }

    });

  }
}