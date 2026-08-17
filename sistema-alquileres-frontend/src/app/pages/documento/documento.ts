import {
  Component,
  Input,
  Output,
  EventEmitter,
  OnChanges,
  SimpleChanges,
  ChangeDetectorRef,
  ViewEncapsulation
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DocumentoService } from './documento.service';

@Component({
  selector: 'app-documento',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './documento.html',
  styleUrls: ['./documento.css'],
  encapsulation: ViewEncapsulation.None
})
export class Documento implements OnChanges {

  @Input() inquilino: any;
     @Input() soloLectura: boolean = false;
  @Output() cerrarModal = new EventEmitter<void>();

  documentos: any[] = [];

  modo: 'listar' | 'crear' = 'listar';

  file: File | null = null;

  documentoForm = { tipdoc: '' };

  errorMsg: string = '';

  constructor(
    private documentoService: DocumentoService,
    private cdr: ChangeDetectorRef
  ) { }

  // ✅ SOLO cuando cambia el inquilino
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['inquilino'] && this.inquilino?.codinq) {

      console.log('NG ON CHANGES');

      this.modo = 'listar';
      this.resetForm();

      this.cargar();

      // 🔥 fuerza actualización inmediata del UI
      this.cdr.detectChanges();
    }
  }

  resetForm(): void {
    this.file = null;
    this.documentoForm = { tipdoc: '' };
  }


cargar(): void {

  this.documentoService
    .listarFiltrado(
      this.inquilino.codinq,
      this.filtroTipo,
      this.soloActivos
    )
    .subscribe({
      next: (data) => {
        this.documentos = data;
        this.cdr.markForCheck();
      },
      error: (err) => console.error(err)
    });
}

  irCrear(): void {
    this.modo = 'crear';
    this.resetForm();
    this.cdr.detectChanges();
       if (this.soloLectura) return;
  }

  irListar(): void {
    this.modo = 'listar';
    this.cdr.detectChanges();
  }

  onFileChange(event: any): void {
  const file: File = event.target.files[0];

  if (!file) return;

  const maxSize = 10 * 1024 * 1024; // 10MB

  if (file.size > maxSize) {
    this.errorMsg = 'El archivo supera el tamaño permitido (10MB)';
    this.file = null;
    event.target.value = '';
    return;
  }

  this.file = file;
  this.errorMsg = '';
}

  puedeGuardar(): boolean {
    return !!this.file && !!this.documentoForm.tipdoc;
  }

  subirDocumento(): void {

    if (!this.file || !this.documentoForm.tipdoc) {
      this.errorMsg = 'Completa todos los campos antes de guardar';
      return;
    }

    const formData = new FormData();
    formData.append('file', this.file);
    formData.append('tipdoc', this.documentoForm.tipdoc);
    formData.append('codinq', this.inquilino.codinq);
    formData.append('codusu', this.inquilino.codusu);

    this.documentoService.crear(formData)
      .subscribe({
        next: () => {

          this.irListar();
          this.resetForm();

          this.cargar();

          this.cdr.detectChanges();
        },
        error: (err) => console.error(err)
      });
  }

  descargarDocumento(d: any): void {
    window.location.href = `http://localhost:8080/documentos/descargar/${d.coddoc}`;
  }

  restaurarDocumento(d:any):void{
    this.documentoService.restaurar(d.coddoc).subscribe({
      next: ()=>{
        this.cargar();
      },
      error: (err)=>console.error(err)
    });
  }

  filtroTipo: string = '';



  cerrar(): void {
    this.cerrarModal.emit();
  }

  soloActivos: boolean = false;


}
