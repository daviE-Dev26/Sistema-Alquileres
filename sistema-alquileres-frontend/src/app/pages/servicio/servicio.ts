import {
  Component,
  Output,
  EventEmitter,
  OnInit,
  ChangeDetectorRef,
  ViewEncapsulation
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ServicioService } from './servicio.service';

@Component({
  selector: 'app-servicio',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './servicio.html',
  styleUrls: ['./servicio.css'],
  encapsulation: ViewEncapsulation.None
})
export class Servicio implements OnInit {

  @Output() cerrarModal = new EventEmitter<void>();
  @Output() actualizarMetricas = new EventEmitter<void>();
  servicios: any[] = [];
  sedes: any[] = [];
  filtroSede: number | null = null;

  modo: 'listar' | 'crear' | 'editar' = 'listar';

  filtroTipo: string = '';

  servicioForm: any = {
    codserv: null,
    tipserv: '',
    monto: null,
    coment: '',
    codusu: null,
    codsede: null
  };

  usuario: any;

  errorMsg: string = '';

  constructor(
    private servicioService: ServicioService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.usuario = JSON.parse(localStorage.getItem('usuario') || '{}');
    this.cargarSedes();
    this.cargar();
  }

  cargarSedes(): void {
    this.servicioService.listarSedes(this.usuario.codusu).subscribe({
      next: (data) => {
        this.sedes = data;
      },
      error: (err) => console.error(err)
    });
  }

  cargar(): void {
    if (!this.usuario?.codusu) return;

    if (this.filtroSede) {
      this.servicioService
        .listarPorSede(this.usuario.codusu, this.filtroSede)
        .subscribe({
          next: (data) => {
            this.servicios = data;
            this.cdr.detectChanges();
          },
          error: (err) => console.error(err)
        });

    } else if (this.filtroTipo) {
      this.servicioService
        .listarPorTipo(this.usuario.codusu, this.filtroTipo)
        .subscribe({
          next: (data) => {
            this.servicios = data;
            this.cdr.detectChanges();
          },
          error: (err) => console.error(err)
        });

    } else {
      this.servicioService
        .listarServicios(this.usuario.codusu)
        .subscribe({
          next: (data) => {
            this.servicios = data;
            this.cdr.detectChanges();
          },
          error: (err) => console.error(err)
        });
    }
  }

  resetForm(): void {
    this.servicioForm = {
      codserv: null,
      tipserv: '',
      monto: null,
      coment: '',
      codusu: this.usuario?.codusu,
      codsede: null
    };

    this.errorMsg = '';
  }

  irCrear(): void {
    this.modo = 'crear';
    this.resetForm();
  }

  irEditar(servicio: any): void {
    this.modo = 'editar';

    this.servicioForm = {
      codserv: servicio.codserv,
      tipserv: servicio.tipserv,
      monto: servicio.monto,
      coment: servicio.coment,
      codusu: this.usuario.codusu,
      codsede: servicio.sede?.codsede
    };
  }

  irListar(): void {
    this.modo = 'listar';
    this.resetForm();
  }

  puedeGuardar(): boolean {
    return !!this.servicioForm.tipserv &&
      this.servicioForm.monto !== null &&
      this.servicioForm.monto > 0 &&
      !!this.servicioForm.codsede;
  }

  guardar(): void {
    if (!this.puedeGuardar()) {
      this.errorMsg = 'Completa todos los campos correctamente.';
      return;
    }

    const payload = {
      tipserv: this.servicioForm.tipserv,
      monto: this.servicioForm.monto,
      coment: this.servicioForm.coment,
      codusu: this.usuario.codusu,
      codsede: this.servicioForm.codsede
    };

    this.servicioService.crear(payload).subscribe({
      next: () => {
        this.actualizarMetricas.emit();
        this.irListar();
        this.cargar();
      },
      error: (err) => {
        console.error(err);
        this.errorMsg = 'Error al registrar el servicio.';
      }
    });
  }

  actualizar(): void {
    if (!this.puedeGuardar()) {
      this.errorMsg = 'Completa todos los campos correctamente.';
      return;
    }

    const payload = {
      tipserv: this.servicioForm.tipserv,
      monto: this.servicioForm.monto,
      coment: this.servicioForm.coment,
      codusu: this.usuario.codusu,
      codsede: this.servicioForm.codsede
    };

    this.servicioService.actualizar(this.servicioForm.codserv, payload)
      .subscribe({
        next: () => {
          this.actualizarMetricas.emit();
          this.irListar();
          this.cargar();
        },
        error: (err) => {
          console.error(err);
          this.errorMsg = 'Error al actualizar el servicio.';
        }
      });
  }

  cerrar(): void {
    this.cerrarModal.emit();
  }

  formatearParaInputDateTime(fecha: string): string {
    if (!fecha) return '';
    return fecha.substring(0, 16);
  }
}