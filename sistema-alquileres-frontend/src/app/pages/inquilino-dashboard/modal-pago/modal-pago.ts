import { Component, Input, Output, EventEmitter, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { PagoService } from '../../services/pago.service';
import { CulqiService } from '../../services/culqi.service';

@Component({
  selector: 'app-modal-pago',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './modal-pago.html',
  styleUrls: ['./modal-pago.css']
})
export class ModalPago {

  @Input() codasig!: number;

  @Input() precioMensual!: number;

  @Input() mesesPendientes = 1;

  @Input() correo = '';

  @Output() cerrar =
  new EventEmitter<void>();

  cantidadMeses = 1;

  observacion = '';

  metodoPago = 'CULQI';

  constructor(
    private culqiService: CulqiService,
    private pagoService: PagoService,
    private cdr: ChangeDetectorRef
  ) {}

  get total(): number {

    return this.precioMensual *
           this.cantidadMeses;

  }

  aumentarMes(){

    if(this.cantidadMeses <
       this.mesesPendientes){

      this.cantidadMeses++;

    }

  }

  disminuirMes(){

    if(this.cantidadMeses > 1){

      this.cantidadMeses--;

    }

  }

  cancelar(){

    this.cerrar.emit();

  }

  pagar(){

    const monto =
    this.total;

    this.culqiService.abrirCheckout(

      monto,

      this.correo,

      (token:string)=>{

        const body = {

          codasig:this.codasig,

          cantidadMeses:this.cantidadMeses,

          metodoPago:'CULQI',

          token:token,

          observacion:this.observacion

        };

        this.pagoService
        .confirmarPago(body)
        .subscribe({

          next:()=>{

            alert(
              'Pago realizado correctamente'
            );

            this.cerrar.emit();
            this.cdr.detectChanges();
          },

          error:(err)=>{

            console.error(err);

            alert(
              'No se pudo realizar el pago'
            );

          }

        });

      }

    );

  }

}