import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ModalEdicion } from './modal-edicion/modal-edicion';
import { ModalPago } from './modal-pago/modal-pago';
import { Documento } from '../documento/documento';
import { DocumentoService } from '../documento/documento.service';
import { PagoService } from './pago.service'; // <-- NUEVO IMPORT

@Component({
  selector: 'app-inquilino-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, ModalEdicion, ModalPago, Documento],
  templateUrl: './inquilino-dashboard.html',
  styleUrls: ['./inquilino-dashboard.css']
})
export class InquilinoDashboard implements OnInit {
  
  datosCuarto: any;
  
  mostrarDocumentosInquilino = false;
  cargandoDocumentos = false;       
  datosInquilinoActual: any = null;

  mostrarModalTransferencia = false;
  archivoTransferencia: File | null = null;
  errorTransferencia = '';
  exitoTransferencia = false;

  // ✅ VARIABLES PARA PAGOS DINÁMICOS
  pagosInquilino: any[] = [];

  constructor(
    private router: Router,
    private cdr: ChangeDetectorRef,
    private documentoService: DocumentoService,
    private pagoService: PagoService // <-- NUEVA INYECCIÓN
  ) { }
  
  mostrarModalPago = false;
  mostrarModalEdicion = false;

  ngOnInit() {
    const session = localStorage.getItem('loginData');

    if (session) {
      this.datosCuarto = JSON.parse(session);
      this.cdr.detectChanges();
      
      // ✅ CARGAR PAGOS AL INICIAR
      this.cargarPagosInquilino(); 
    } else {
      this.router.navigate(['/login/inquilino']);
    }
  }

  getIniciales(): string {
    if (!this.datosCuarto) return '';
    const nombre = this.datosCuarto.nominq?.trim() || '';
    const apellido = this.datosCuarto.apepinq?.trim() || '';
    return (
      (nombre.charAt(0) || '') +
      (apellido.charAt(0) || '')
    ).toUpperCase();
  }

  getNombreMostrar(): string {
    if (!this.datosCuarto) return '';
    const primerNombre = this.datosCuarto.nominq?.trim().split(' ')[0] || '';
    const apellido = this.datosCuarto.apepinq?.trim() || '';
    return `${primerNombre} ${apellido}`;
  }

  cerrarSesion() {
    localStorage.removeItem('loginData');
    this.router.navigate(['/login/inquilino']);
  }

  actualizarDatosInquilino(dataActualizada: any) {
    this.datosCuarto = {
      ...this.datosCuarto,
      celinq: dataActualizada.celinq,
      corinq: dataActualizada.corinq
    };
    localStorage.setItem('loginData', JSON.stringify(this.datosCuarto));
    this.mostrarModalEdicion = false;
  }

  abrirDocumentosInquilino() {
    this.datosInquilinoActual = {
      codinq: this.datosCuarto?.codinq, 
      nombreCompleto: this.getNombreMostrar(),
      dni: this.datosCuarto?.docinq
    };
    this.mostrarDocumentosInquilino = true;
  }

  cerrarDocumentosInquilino() {
    this.mostrarDocumentosInquilino = false;
    this.datosInquilinoActual = null;
  }

  onFileTransferenciaChange(event: any): void {
    const file: File = event.target.files[0];
    if (file) {
      if (file.size > 5 * 1024 * 1024) {
        this.errorTransferencia = 'El archivo es muy grande (Máximo 5MB)';
        this.archivoTransferencia = null;
        event.target.value = '';
        return;
      }
      this.archivoTransferencia = file;
      this.errorTransferencia = '';
      this.exitoTransferencia = false;
    }
  }

  subirComprobanteTransferencia(): void {
    if (!this.archivoTransferencia) {
      this.errorTransferencia = 'Debe seleccionar un archivo.';
      return;
    }

    const codinq = this.datosCuarto?.codinq;
    if (!codinq) {
      this.errorTransferencia = 'Error: No se pudo identificar al inquilino.';
      return;
    }

    let codusu = this.datosCuarto?.codusu;
    if (!codusu) {
      const usuarioStorage = JSON.parse(localStorage.getItem('usuario') || '{}');
      codusu = usuarioStorage.codusu || 1;
    }

    const formData = new FormData();
    formData.append('file', this.archivoTransferencia);
    formData.append('tipdoc', 'recibo');
    formData.append('codinq', codinq.toString());
    formData.append('codusu', codusu.toString());

    this.documentoService.crear(formData).subscribe({
      next: () => {
        this.exitoTransferencia = true;
        this.archivoTransferencia = null;
        setTimeout(() => this.cerrarModalTransferencia(), 2000);
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('❌ Error del backend:', err);
        this.errorTransferencia = err.error?.message || err.error || 'Error desconocido al subir el archivo.';
      }
    });
  }

  cerrarModalTransferencia(): void {
    this.mostrarModalTransferencia = false;
    this.archivoTransferencia = null;
    this.errorTransferencia = '';
    this.exitoTransferencia = false;
    this.cdr.detectChanges();
  }

  // ==========================================
  // MÉTODOS NUEVOS PARA PAGOS DINÁMICOS
  // ==========================================
  
  cargarPagosInquilino(): void {
    if (this.datosCuarto?.codinq) {
      this.pagoService.obtenerProximosPagos(this.datosCuarto.codinq).subscribe({
        next: (pagos) => {
          this.pagosInquilino = pagos;
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Error al cargar pagos:', err)
      });
    }
  }

  getNombreMes(mes: number): string {
    const meses = [
      'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
      'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'
    ];
    return meses[mes - 1] || '';
  }

  getBadgeEstado(estado: string): string {
    if (estado === 'PAGADO' || estado === 'APROBADO') return 'badge-success';
    if (estado === 'PENDIENTE') return 'badge-pending';
    return 'badge-secondary';
  }

  getTextoEstado(estado: string): string {
    if (estado === 'PAGADO' || estado === 'APROBADO') return 'Pagado';
    if (estado === 'PENDIENTE') return 'Pendiente';
    return estado;
  }
}