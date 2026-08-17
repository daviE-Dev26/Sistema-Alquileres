 import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { InquilinoService } from '../../propietario-dashboard/inquilino.service';

@Component({
  selector: 'app-modal-edicion',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './modal-edicion.html',
  styleUrl: './modal-edicion.css',
})
export class ModalEdicion implements OnInit {

  @Input() datosInquilino: any;
  @Output() cerrar = new EventEmitter<void>();
  @Output() actualizado = new EventEmitter<any>();

  celular: string = '';
  correo: string = '';
  errorMessage: string = '';
  guardando: boolean = false;

  constructor(private inquilinoService: InquilinoService) {}

  ngOnInit(): void {
    this.celular = this.datosInquilino?.celinq || '';
    this.correo = this.datosInquilino?.corinq || '';
  }

  cerrarModal() {
    this.cerrar.emit();
  }

  guardarCambios() {
    this.errorMessage = '';

    if (!/^9\d{8}$/.test(this.celular)) {
      this.errorMessage = 'El celular debe iniciar con 9 y tener 9 dígitos.';
      return;
    }

    const correoRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!correoRegex.test(this.correo)) {
      this.errorMessage = 'Ingresa un correo válido.';
      return;
    }

    const payload = {
      celinq: this.celular,
      corinq: this.correo
    };

    this.guardando = true;

    this.inquilinoService
      .actualizarContactoPortal(this.datosInquilino.codinq, payload)
      .subscribe({
        next: (resp) => {
          this.guardando = false;
          this.actualizado.emit(resp);
          this.cerrar.emit();
        },
        error: (err) => {
          this.guardando = false;
          this.errorMessage =
            typeof err.error === 'string'
              ? err.error
              : 'No se pudo actualizar el perfil.';
        }
      });
  }
}