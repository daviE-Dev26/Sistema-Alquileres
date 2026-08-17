import {
  Component,
  EventEmitter,
  Output,
  OnInit,
  ChangeDetectorRef
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { InquilinoService } from '../inquilino.service';

@Component({

  selector: 'app-modal-historicos',

  standalone: true,

  imports: [
    CommonModule,
    FormsModule
  ],

  templateUrl: './modal-historicos.html',

  styleUrls: ['./modal-historicos.css']

})

export class ModalHistoricos implements OnInit {

  @Output()

  cerrarModal =
    new EventEmitter<void>();

  usuario: any;

  tabSeleccionada: 'datos' | 'cuartos' = 'datos';

  dniDatos = '';

  dniCuartos = '';

  historicoDatos: any[] = [];

  historicoCuartos: any[] = [];

  cargandoDatos = false;

  cargandoCuartos = false;

  mensajeDatos = '';

  mensajeCuartos = '';

  constructor(

    private inquilinoService: InquilinoService,
    private cdr: ChangeDetectorRef

  ) { }

  ngOnInit(): void {

    this.usuario =
      JSON.parse(
        localStorage.getItem('usuario') || '{}'
      );

  }

  seleccionarTab(tab: 'datos' | 'cuartos') {

    this.tabSeleccionada = tab;
    this.cdr.detectChanges();


  }

  buscarHistoricoDatos(): void {

    this.historicoDatos = [];
    this.mensajeDatos = '';

    const dni = this.dniDatos.trim();

    if (!/^\d{8}$/.test(dni)) {
      this.mensajeDatos = 'Ingrese un DNI válido.';
      return;
    }

    this.cargandoDatos = true;
    this.cdr.detectChanges();

    this.inquilinoService
      .obtenerHistoricoDatos(
        dni,
        this.usuario.codusu
      )
      .subscribe({

        next: data => {
          this.cargandoDatos = false;

          this.historicoDatos = data || [];

          if (this.historicoDatos.length === 0) {
            this.mensajeDatos =
              'No se encontraron registros.';
          }


          this.cdr.detectChanges();

        },

        error: error => {

          console.error(
            'Error al consultar historial de datos:',
            error
          );

          this.mensajeDatos =
            'Error al consultar el historial.';

          this.cargandoDatos = false;

          this.cdr.detectChanges();

        }

      });

  }

  buscarHistoricoCuartos(): void {

    this.historicoCuartos = [];
    this.mensajeCuartos = '';

    const dni = this.dniCuartos.trim();

    if (!/^\d{8}$/.test(dni)) {
      this.mensajeCuartos =
        'Ingrese un DNI válido.';
      return;
    }

    this.cargandoCuartos = true;
    this.cdr.detectChanges();

    this.inquilinoService
      .obtenerHistoricoCuartos(
        dni,
        this.usuario.codusu
      )
      .subscribe({

        next: data => {

          console.log(
            'Histórico de cuartos recibido:',
            data
          );
          this.cargandoCuartos = false;

          this.historicoCuartos = data || [];

          if (this.historicoCuartos.length === 0) {
            this.mensajeCuartos =
              'No se encontraron registros.';
          }


          this.cdr.detectChanges();

        },

        error: error => {

          console.error(
            'Error al consultar historial de cuartos:',
            error
          );

          this.mensajeCuartos =
            'Error al consultar el historial.';

          this.cargandoCuartos = false;

          this.cdr.detectChanges();

        }

      });

  }

  cerrar() {

    this.cerrarModal.emit();

  }

  verTodosHistoricoDatos(): void {

    this.historicoDatos = [];
    this.mensajeDatos = '';

    this.cargandoDatos = true;
    this.cdr.detectChanges();

    this.inquilinoService
      .obtenerTodosHistoricoDatos(this.usuario.codusu)
      .subscribe({

        next: data => {

          this.cargandoDatos = false;

          this.historicoDatos = data || [];

          if (this.historicoDatos.length === 0) {
            this.mensajeDatos =
              'No se encontraron registros.';
          }

          this.cdr.detectChanges();

        },

        error: error => {

          console.error(
            'Error al consultar historial:',
            error
          );

          this.mensajeDatos =
            'Error al consultar el historial.';

          this.cargandoDatos = false;

          this.cdr.detectChanges();

        }

      });

  }
  verTodosHistoricoCuartos(): void {

    this.historicoCuartos = [];
    this.mensajeCuartos = '';

    this.cargandoCuartos = true;
    this.cdr.detectChanges();

    this.inquilinoService
      .obtenerTodosHistoricoCuartos(this.usuario.codusu)
      .subscribe({

        next: data => {

          this.cargandoCuartos = false;

          this.historicoCuartos = data || [];

          if (this.historicoCuartos.length === 0) {
            this.mensajeCuartos =
              'No se encontraron registros.';
          }

          this.cdr.detectChanges();

        },

        error: error => {

          console.error(
            'Error al consultar historial:',
            error
          );

          this.mensajeCuartos =
            'Error al consultar el historial.';

          this.cargandoCuartos = false;

          this.cdr.detectChanges();

        }

      });

  }
}