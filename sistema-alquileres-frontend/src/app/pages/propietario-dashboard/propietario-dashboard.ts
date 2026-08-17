import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { HttpClient } from '@angular/common/http'; // <-- 1. AGREGADO
import { CuartoService } from './cuarto.service';
import { UsuarioService } from './usuario.service';
import { ReniecService } from './reniec.service';
import { InquilinoService } from './inquilino.service';
import { Documento } from '../documento/documento';
import { SedeService } from './sede.service';
import { PisoService } from './piso.service';
import { Servicio } from '../servicio/servicio';
import { ModalMetricas } from './modal-metricas/modal-metricas';
import { MetricasService } from './modal-metricas/metricas.service';
import { ModalHistoricos } from './modal-historicos/modal-historicos';
@Component({
  selector: 'app-propietario-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, Documento, Servicio, ModalMetricas, ModalHistoricos],
  templateUrl: './propietario-dashboard.html',
  styleUrl: './propietario-dashboard.css',
})
export class PropietarioDashboard implements OnInit {
  // =========================================================
  // 1. CONTROL GENERAL DEL DASHBOARD
  // =========================================================

  modo: 'listar' | 'crear' | 'editar' = 'listar';

  // 📑 Control para alternar entre pestañas
  subModo: 'sedes' | 'pisos' | 'cuartos' | 'inquilinos'|'reservas' = 'sedes';
 mostrarModalHistoricos = false;
  usuario: any;
  // =========================================================
  // 2. DATOS DE SEDES
  // =========================================================

  sedes: any[] = [];

  sedeSeleccionada: any = null;

  sedeForm: any = {
    nombre: '',
    direccion: '',
    descripcion: '',
    cantidadPisos: 1,
    cuartosPorPiso: [1],
    precioCuarto: 0
  };

  // =========================================================
  // 3. DATOS DE PISOS
  // =========================================================

  pisos: any[] = [];
  totalPisos = 0;
  pisoSeleccionado: any = null;
  
  inquilinoForm = this.crearInquilinoForm();

  // =========================================================
  // 4. DATOS DE CUARTOS
  // =========================================================
  cuartos: any[] = [];
  cuartoSeleccionado: any = null;
  cuartosDisponibles: any[] = [];
  cuarto: any = {
    codcuar: null,
    numcuar: '',
    dircuar: '',
    preccuar: '',
    descuar: '',
    estcuar: '',
    passcuar: '',
    fotocuar: '',
    codusu: null,
    codpiso: null
  };
  nuevoNumeroCuarto: number | null = null;
  precioSugerido: number = 0;

  editarCuarto: any = {
    codcuar: null,
    codsede: null,
    nombreSede: '',
    codpiso: null,
    numeroPiso: null,
    numcuar: null,
    dircuar: '',
    preccuar: 0,
    passcuar: '',
    descuar: '',
    estcuar: ''
  };

  // =========================================================
  // 5. DATOS DE INQUILINOS
  // =========================================================
  inquilinoSeleccionadoId: number | null = null;
  codInquilinoSeleccionado:number|null = null;
  codAsignacionSeleccionada:number|null = null;
  
  inquilinos: any[] = [];

  inquilino: any = {
    nombres: '',
    apellidoPaterno: '',
    apellidoMaterno: '',
  };
  
  sedesInquilino:any[]=[];
  pisosDisponibles:any[]=[];
  codsedeSeleccionada:number|null=null;
  codpisoSeleccionado:number|null=null;
  mostrarPanelMoverCuarto = false;

  codSedeOriginal: number | null = null;
  codPisoOriginal: number | null = null;
  codCuartoOriginal: number | null = null;

  codSedeNueva: number | null = null;
  codPisoNueva: number | null = null;
  codCuartoNueva: number | null = null;

  pisosDisponiblesMover: any[] = [];
  cuartosDisponiblesMover: any[] = [];

  // =========================================================
  // 5.1 DATOS DE METRICAS
  // =========================================================
  metricas:any;

  // =========================================================
  // 5.5 DATOS DE RESERVAS
  // =========================================================
  reservas: any[] = [];

  // =========================================================
  // 6. DOCUMENTOS
  // =========================================================
  mostrarGestionServicio = false;
  mostrarDocumentos = false;
  inquilinoSeleccionado: any = null;

  // =========================================================
  // 7. MODALES
  // =========================================================
  mostrarModalCuarto: boolean = false;
  mostrarModalInquilino: boolean = false;
  mostrarModalSede = false;
  mostrarModalPiso = false;
  mostrarModalEditarCuarto = false;
  mostrarModalContratosFinalizados = false;
  contratosFinalizados:any[] = [];
  mostrarModalEditarInquilino = false;

  // =========================================================
  // 8. ALERTAS
  // =========================================================
  mostrarAlertaExitoSede = false;
  mostrarAlertaExitoInquilino: boolean = false;
  mostrarAlertaExitoCuarto: boolean = false;
  mostrarErroresSede = false;
  pisoTocado: boolean[] = [];

  // =========================================================
  // 9. BÚSQUEDAS
  // =========================================================
  vistaTarjetas = true;
  dniBusqueda: string = '';
  
  constructor(
    private http: HttpClient, // <-- 2. AGREGADO
    private cuartoService: CuartoService,
    private usuarioService: UsuarioService,
    private reniecService: ReniecService,
    private inquilinoService: InquilinoService,
    private sedeService: SedeService,
    private pisoService: PisoService,
    private cdr: ChangeDetectorRef,
    private metricasService:MetricasService
  ) { }

  // METODOS
  // =========================================================
  // 1. CICLO DE VIDA
  // =========================================================
  ngOnInit(): void {
    this.usuario=JSON.parse(localStorage.getItem('usuario') || '{}');
    this.listarSedes();
    this.listar();
    this.listarCuartosDisponibles();
    this.listarInquilinos();
    this.cargarMetricas();
  }

  // =========================================================
  // 2. NAVEGACIÓN INTERNA
  // =========================================================
  cambiarSubModo(
    pestana: 'sedes' | 'pisos' | 'cuartos' | 'inquilinos' | 'reservas'
  ): void {
    this.subModo = pestana;
    if (pestana === 'reservas') {
      this.cargarReservas(this.usuario.codusu);
    }

    if (pestana === 'inquilinos') {
      if (this.cuartoSeleccionado) {
        this.listarInquilinosPorCuarto(this.cuartoSeleccionado.codcuar);
      } else if (this.pisoSeleccionado) {
        this.listarInquilinosPorPiso(this.pisoSeleccionado.codpiso);
      } else if (this.sedeSeleccionada) {
        this.listarInquilinosPorSede(this.sedeSeleccionada.codsede);
      } else {
        this.listarInquilinos();
      }
    }

    if (pestana === 'cuartos') {
      if (this.pisoSeleccionado) {
        this.listarCuartosPorPiso(this.pisoSeleccionado.codpiso);
      } else if (this.sedeSeleccionada) {
        this.listarCuartosPorSede(this.sedeSeleccionada.codsede);
      } else {
        this.listar();
      }
    }

    if (pestana === 'pisos') {
      if(this.sedeSeleccionada){
        this.listarPisos(this.sedeSeleccionada.codsede);
      } else {
        this.pisoService.listarTodosLosPisos(this.usuario.codusu).subscribe(data=>{
          this.pisos = data;
          this.cdr.detectChanges();
        });
      }
    }
  }

  cargarReservas(codusu: number): void {
    this.inquilinoService.getReservas(codusu).subscribe({
      next: (data: any[]) => {
        this.reservas = data;
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        console.error('Error al cargar las reservas del propietario:', err);
      }
    });
  }

  filtroActivo = {
    sede: null,
    piso: null,
    cuarto: null
  };

  limpiarFiltros(): void {
    this.pisoSeleccionado = null;
    this.cuartoSeleccionado = null;
    this.inquilinoSeleccionado=null;

    this.filtroActivo = {
      sede: this.sedeSeleccionada?.codsede ?? null,
      piso: null,
      cuarto: null
    };

    this.refrescarVistaActual();
  }

  refrescarVistaActual(): void {
    if (this.subModo === 'inquilinos') {
      if (this.cuartoSeleccionado) {
        this.listarInquilinosPorCuarto(this.cuartoSeleccionado.codcuar);
      } else if (this.pisoSeleccionado) {
        this.listarInquilinosPorPiso(this.pisoSeleccionado.codpiso);
      } else if (this.sedeSeleccionada) {
        this.listarInquilinosPorSede(this.sedeSeleccionada.codsede);
      } else {
        this.listarInquilinos();
      }
    } else {
      if (this.pisoSeleccionado) {
        this.listarCuartosPorPiso(this.pisoSeleccionado.codpiso);
      } else if (this.sedeSeleccionada) {
        this.listarCuartosPorSede(this.sedeSeleccionada.codsede);
      } else {
        this.listar();
      }
    }
    this.cargarMetricas();
  }

  // =========================================================
  // 3. SEDES
  // =========================================================
  listarSedes(): void {
    const usuario = JSON.parse(localStorage.getItem('usuario')!);

    this.sedeService.listarPorUsuario(usuario.codusu).subscribe({
      next: (data) => {
        this.refrescarVistaActual();
        this.sedes = data;
        this.totalPisos = 0;
        this.sedes.forEach(sede => {
          this.totalPisos += sede.cantidadPisos;
        });
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
      }
    });
  }

  abrirModalSede(): void {
    this.mostrarModalSede = true;
  }

  cerrarModalSede(): void {
    this.mostrarModalSede = false;
  }

  crearSede(): void {
    const usuario = JSON.parse(localStorage.getItem('usuario')!);

    const nuevaSede = {
      nombre: this.sedeForm.nombre,
      direccion: this.sedeForm.direccion,
      descripcion: this.sedeForm.descripcion,
      codusu: usuario.codusu,
      cantidadPisos: this.sedeForm.cantidadPisos,
      cuartosPorPiso: this.sedeForm.cuartosPorPiso
    };

    this.sedeService.crearMasiva(nuevaSede).subscribe({
      next: () => {
        this.listarSedes();
        this.sedeForm = {
          nombre: '',
          direccion: '',
          descripcion: '',
          cantidadPisos: 1,
          cuartosPorPiso: [1],
          cuartosGlobal: null,
          codusu: this.usuario.codusu,
          precioCuarto: 0
        };
        this.cerrarModalSede();
      },
      error: err => {
        console.error(err);
        alert("Error al registrar la sede");
      }
    });
  }

  seleccionarSede(sede: any) {
    this.sedeSeleccionada = sede;
    this.subModo = 'pisos';
    this.pisoSeleccionado = null;
    this.cuartoSeleccionado = null;

    this.filtroActivo = {
      sede: sede.codsede,
      piso: null,
      cuarto: null
    };

    this.listarPisos(sede.codsede);
    this.listarCuartosPorSede(sede.codsede);
    this.listarInquilinosPorSede(sede.codsede);
  }

  // =========================================================
  // 4. PISOS
  // =========================================================
  listarPisos(codsede: number): void {
    this.pisoService.listarResumenPorSede(codsede).subscribe({
      next: (data) => {
        this.pisos = data;
        console.log('Pisos cargados:', data);
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
      }
    });
  }

  seleccionarPiso(piso: any) {
    this.pisoSeleccionado = piso;
    this.filtroActivo = {
      sede: this.filtroActivo.sede,
      piso: piso.codpiso,
      cuarto: null
    };

    this.listarCuartosPorPiso(piso.codpiso);
    this.listarInquilinosPorPiso(piso.codpiso);
    this.subModo = 'cuartos';
  }

  listarTodosLosPisos(): void {
    if (!this.usuario) return;

    this.pisoService.listarTodosLosPisos(this.usuario.codusu).subscribe({
      next: (data) => {
        this.pisos = data;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
      }
    });
  }

  cargarPisos():void{  
    this.codpisoSeleccionado = null;
    this.nuevoNumeroCuarto = null;
    this.precioSugerido = 0;
    this.cuartosDisponibles = [];
    
    const sede = this.sedes.find(s => s.codsede === this.codsedeSeleccionada);

    if (!this.codsedeSeleccionada) {
      this.pisosDisponibles = [];
      this.codpisoSeleccionado = null;
      this.cuartosDisponibles = [];
      this.inquilinoForm.codcuar = null;
      this.nuevoNumeroCuarto = null;
      this.precioSugerido = 0;
      this.cuarto.dircuar = '';
      this.inquilinoForm.codcuar = null;
      return;
    }

    this.codpisoSeleccionado = null;
    this.inquilinoForm.codcuar = null;
    this.cuartosDisponibles=[];
    
    this.pisoService.listarPorSede(this.codsedeSeleccionada!).subscribe({
      next:(data)=>{
        this.pisosDisponibles=data;
      },
      error:(err)=>console.error(err)
    });
    
    console.log('SEDE ENCONTRADA:', sede);
    if (sede) {
      this.cuarto.dircuar = sede.direccion;
      this.cdr.detectChanges();
    }
  }

  // =========================================================
  // 5. CUARTOS
  // =========================================================
  listar(): void {
    if (this.usuario && this.usuario.codusu) {
      this.cuartoService.listarCuartos(this.usuario.codusu).subscribe({
        next: (data) => {
          console.log(data);
          this.cuartos = data;
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Error al listar cuartos:', err);
        },
      });
    }
  }

  listarCuartosDisponibles() {
    if (this.usuario && this.usuario.codusu) {
      this.cuartoService.listarDisponibles(this.usuario.codusu).subscribe({
        next: (data) => {
          this.cuartosDisponibles = data;
        },
        error: (err) => console.error('Error al listar cuartos disponibles:', err),
      });
    }
  }

  listarCuartosPorPiso(codpiso: number): void {
    this.cuartoService.listarPorPiso(codpiso).subscribe({
      next: (data) => {
        console.log(data);
        this.cuartos = data;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
      }
    });
  }

  listarCuartosPorSede(codsede: number): void {
    this.cuartoService.listarPorSede(codsede).subscribe({
      next: (data) => {
        console.log(data);
        this.cuartos = data;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
      }
    });
  }
  
  cargarCuartos(): void {
    this.inquilinoForm.codcuar = null;
    if (!this.codpisoSeleccionado) {
      this.cuartosDisponibles = [];
      return;
    }

    this.cuartoService.listarDisponiblesPorPiso(this.codpisoSeleccionado).subscribe({
      next:(data)=>{
        this.cuartosDisponibles = data;
        this.cdr.detectChanges();
      },
      error:(err)=>console.error(err)
    });
  }

  verCuartosSede(): void {
    if (!this.sedeSeleccionada) return;
    this.cuartoSeleccionado = null;
    this.listarCuartosPorSede(this.sedeSeleccionada.codsede);
    this.subModo = 'cuartos';
  }

  seleccionarCuarto(cuarto: any): void {
    this.cuartoSeleccionado = cuarto;
    this.filtroActivo = {
      sede: this.filtroActivo.sede,
      piso: this.filtroActivo.piso,
      cuarto: cuarto.codcuar
    };
    this.listarInquilinosPorCuarto(cuarto.codcuar);
    this.subModo = 'inquilinos';
    this.cdr.detectChanges();
  }

  actualizarCuartosPorPiso(): void {
    const cantidad = Number(this.sedeForm.cantidadPisos || 0);
    this.sedeForm.cuartosPorPiso = Array.from({ length: cantidad }, () => 1);
  }

  limpiar(): void {
    this.cuarto = {
      codcuar: null,
      dircuar: '',
      preccuar: '',
      descuar: '',
      estcuar: 'Disponible',
      passcuar: '',
      fotocuar: 'default.jpg',
      codusu: this.usuario?.codusu || null
    };
    this.codsedeSeleccionada = null;
    this.codpisoSeleccionado = null;
    this.nuevoNumeroCuarto = null;
    this.pisosDisponibles = [];
    this.cuartosDisponibles = [];
  }

  abrirModalCuarto(): void {
    this.limpiar();
    this.mostrarModalCuarto = true;
    this.mostrarModalInquilino = false;
    this.mostrarAlertaExitoCuarto = false;
    this.cuarto.passcuar = this.generarPass();
  }

  cerrarModalCuarto(): void {
    this.mostrarModalCuarto = false;
    this.mostrarAlertaExitoCuarto = false;
  }

  abrirModalEditarCuarto(codcuar: number): void {
    this.mostrarModalEditarCuarto = false;
    this.cuartoService.obtenerPorId(codcuar).subscribe({
      next: (data: any) => {
        this.editarCuarto = { ...data };
        this.mostrarModalEditarCuarto = true;
        this.cdr.detectChanges();
      },
      error: (err: any) => console.error(err)
    });
  }

  cerrarModalEditarCuarto(): void {
    this.mostrarModalEditarCuarto = false;
    this.cdr.detectChanges();
  }

  crear(): void {
    if (
        !this.codsedeSeleccionada ||
        !this.codpisoSeleccionado ||
        !this.nuevoNumeroCuarto ||
        !this.cuarto.dircuar ||
        this.cuarto.dircuar.trim() === '' ||
        !this.cuarto.preccuar ||
        Number(this.cuarto.preccuar) <= 0 ||
        !this.cuarto.passcuar ||
        this.cuarto.passcuar.trim() === ''
    ) {
        alert('Por favor, complete todos los campos obligatorios con datos válidos.');
        return;
    }

    this.cuarto.codusu = this.usuario.codusu;
    const descriptionFinal = this.cuarto.descuar ? this.cuarto.descuar : '';
    const fotoFinal = 'default.jpg';

    const payload = {
      numcuar: this.nuevoNumeroCuarto,
      codsede: this.codsedeSeleccionada,
      numeroPiso: this.codpisoSeleccionado,
      dircuar: this.cuarto.dircuar,
      preccuar: this.cuarto.preccuar,
      descuar: descriptionFinal,
      passcuar: this.cuarto.passcuar,
      fotocuar: fotoFinal,
      estcuar: 'Disponible',
      codusu: this.usuario.codusu,
    };

    this.cuartoService.crearCuarto(payload).subscribe({
      next: () => {
        this.mostrarAlertaExitoCuarto = true;
        this.cdr.detectChanges();

        this.listar();
        this.listarCuartosDisponibles();

        setTimeout(() => {
          this.mostrarAlertaExitoCuarto = false;
          this.cerrarModalCuarto();
          this.limpiar();
          this.cdr.detectChanges();
        }, 2500);
        
        if(this.codpisoSeleccionado){
          this.listarCuartosPorPiso(this.codpisoSeleccionado);
        }
      },
      error: (err) => {
        console.error(err);
        let mensajeError = 'El número de cuarto que se desea ingresar ya se encuentra registrado.';
        if (err.error && typeof err.error === 'string') {
          mensajeError = err.error;
        }
        alert(mensajeError);
      },
    });
  }

  generarPass(): string {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    let result = '';
    for (let i = 0; i < 6; i++) {
      result += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return result;
  }

  seleccionarPisoNuevoCuarto(): void {
    if (!this.codpisoSeleccionado) {
      this.nuevoNumeroCuarto = null;
      this.precioSugerido = 0;
      return;
    }

    this.cuartoService.listarPorPiso(this.codpisoSeleccionado).subscribe({
      next:(cuartos)=>{
        if(cuartos.length === 0){
          const piso = this.pisosDisponibles.find(p => p.codpiso == this.codpisoSeleccionado);
          const numeroPiso = piso.numero;
          this.nuevoNumeroCuarto = numeroPiso * 100 + 1;
          this.cuarto.preccuar = 0;
          return;
        }

        const ultimo = cuartos.sort((a,b) => b.numcuar - a.numcuar)[0];
        this.nuevoNumeroCuarto = ultimo.numcuar + 1;
        this.cuarto.preccuar = ultimo.preccuar;
        this.cdr.detectChanges();
      },
      error:(err)=>{
        console.error(err);
      }
    });
  }

  aplicarCuartosGlobal(): void {
    const valor = Number(this.sedeForm.cuartosGlobal);
    if (!valor || valor < 1) return;

    for (let i = 0; i < this.sedeForm.cuartosPorPiso.length; i++) {
      this.sedeForm.cuartosPorPiso[i] = valor;
    }
    this.sedeForm.cuartosPorPiso = [...this.sedeForm.cuartosPorPiso];
  }

  irEditar(c: any): void {
    this.modo = 'editar';
    this.cuarto = {
      ...c,
      codusu: c.codusu || this.usuario.codusu,
    };
  }

  actualizarCuarto(): void {
    this.cuartoService.actualizarCuarto(this.editarCuarto.codcuar, this.editarCuarto).subscribe({
      next: () => {
        const index = this.cuartos.findIndex(c => c.codcuar === this.editarCuarto.codcuar);
        if (index !== -1) {
          this.cuartos[index] = { ...this.editarCuarto };
        }
        this.cerrarModalEditarCuarto();
        this.cargarMetricas();
        this.cdr.detectChanges();
        this.refrescarVistaActual();
      }
    });
  }

  regenerarCodigoEditar(): void {
    this.editarCuarto.passcuar = this.generarPass();
  }

  habilitar(c: any): void {
    this.cuartoService.cambiarEstado(c.codcuar,'Disponible').subscribe({
      next: () => {
        c.estcuar = 'Disponible';
        if (this.editarCuarto && this.editarCuarto.codcuar === c.codcuar) {
            this.editarCuarto.estcuar = 'Disponible';
        }
        this.refrescarVistaActual();
        this.cdr.detectChanges();
      },
      error: err => console.error(err)
    });
  }

  eliminar(c: any): void {
    this.cuartoService.cambiarEstado(c.codcuar, 'Inhabilitado').subscribe({
      next: () => {
        c.estcuar = 'Inhabilitado';
        if (this.editarCuarto && this.editarCuarto.codcuar === c.codcuar) {
          this.editarCuarto.estcuar = 'Inhabilitado';
        }
        this.refrescarVistaActual();
        this.cdr.detectChanges();
      },
      error: err => console.error(err)
    });
  }

  toggleHabilitado(cuarto: any): void {
    const nuevoEstado = !cuarto.habilitado;
    this.cuartoService.cambiarHabilitado(cuarto.codcuar, nuevoEstado).subscribe({
      next: () => {
        this.refrescarVistaActual();
        this.abrirModalEditarCuarto(cuarto.codcuar);
      },
      error: err => console.error(err)
    });
  }

  cancelar(): void {
    this.modo = 'listar';
    this.limpiar();
  }

  // =========================================================
  // 6. INQUILINOS
  // =========================================================
  listarInquilinos(): void {
    if (this.usuario && this.usuario.codusu) {
      this.inquilinoService.listarDashboard(this.usuario.codusu).subscribe({
        next: (data: any[]) => {
          console.log(data);
          this.inquilinos = data;
          this.cdr.detectChanges();
        },
        error: (err: any) => {
          console.error('Error al listar inquilinos:', err);
        },
      });
    }
  }

  listarInquilinosPorCuarto(codcuar: number): void {
    this.inquilinoService.listarPorCuarto(codcuar).subscribe({
      next: (data) => {
        console.log('INQUILINOS DEL CUARTO', data);
        this.inquilinos = data;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
      }
    });
  }

  listarInquilinosPorSede(codsede: number): void {
    console.log("SEDE FILTRADA:", codsede);
    this.inquilinoService.listarPorSede(codsede).subscribe({
      next: (data) => {
        console.log('INQUILINOS DE LA SEDE', data);
        this.inquilinos = data;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
      }
    });
  }

  listarInquilinosPorPiso(codpiso: number): void {
    this.inquilinoService.listarPorPiso(codpiso).subscribe({
      next: (data) => {
        console.log('INQUILINOS DEL PISO', data);
        this.inquilinos = data;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
      }
    });
  }

  verInquilinosSede(): void {
    this.subModo = 'inquilinos';
    if (this.cuartoSeleccionado) {
        this.listarInquilinosPorCuarto(this.cuartoSeleccionado.codcuar);
    } else if (this.pisoSeleccionado) {
        this.listarInquilinosPorPiso(this.pisoSeleccionado.codpiso);
    } else if (this.sedeSeleccionada) {
        this.listarInquilinosPorSede(this.sedeSeleccionada.codsede);
    } else {
        this.listarInquilinos();
    }
  }

  verDetalleInquilino(i:any){
    this.inquilinoSeleccionado=i;
    this.inquilinos=[i];
    this.subModo='inquilinos';
  }

  private crearInquilinoForm() {
    return {
      codinq: null,
      codasig: null,
      dni: '',
      nombres: '',
      apePaterno: '',
      apeMaterno: '',
      celular: '',
      correo: '',
      fechaInicio: '',
      codcuar: null,
      codsede: null,
      codpiso: null,
      numeroPiso: null,
      numeroCuarto: null,
      nombreSede: ''
    };
  }

  limpiarInquilinoForm(): void {
    this.dniBusqueda = '';
    this.inquilinoSeleccionadoId = null;
    this.inquilinoForm = this.crearInquilinoForm();
    this.codsedeSeleccionada = null;
    this.codpisoSeleccionado = null;
    this.pisosDisponibles = [];
    this.cuartosDisponibles = [];
  }

  abrirModalInquilino(): void {
    this.limpiarInquilinoForm();
    this.mostrarModalInquilino = true;
    this.mostrarModalCuarto = false;
    this.mostrarAlertaExitoInquilino = false;
  }

  cerrarModalInquilino(): void {
    this.mostrarModalInquilino = false;
    this.mostrarAlertaExitoInquilino = false;
    this.inquilinoSeleccionadoId = null;
  }

  buscarDni(): void {
    if (this.dniBusqueda.length !== 8) {
      alert('Ingrese un DNI válido');
      return;
    }
    this.reniecService.consultarDni(this.dniBusqueda).subscribe({
      next: (data: any) => {
        console.log(data);
        this.inquilinoForm.nombres = data.nombres;
        this.inquilinoForm.apePaterno = data.apellidoPaterno;
        this.inquilinoForm.apeMaterno = data.apellidoMaterno;
        this.inquilinoForm.dni = this.dniBusqueda;
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        console.error(err);
        alert('No se pudo consultar RENIEC');
      },
    });
  }

  registrarInquilino(): void {
    const correoRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!this.dniBusqueda) {
      alert('Ingrese DNI');
      return;
    }
    if (!/^\d{8}$/.test(this.dniBusqueda)) {
      alert('El DNI debe tener 8 números');
      return;
    }
    if (this.dniBusqueda.startsWith('000')) {
      alert('DNI inválido');
      return;
    }
    if (!this.inquilinoForm.nombres) {
      alert('Debe consultar RENIEC');
      return;
    }
    if (!this.inquilinoForm.correo) {
      alert('Ingrese correo');
      return;
    }
    if (!correoRegex.test(this.inquilinoForm.correo)) {
      alert('Correo inválido');
      return;
    }
    if (!this.inquilinoForm.celular) {
      alert('Ingrese celular');
      return;
    }
    if (!/^9\d{8}$/.test(this.inquilinoForm.celular)) {
      alert('El celular debe iniciar con 9 y tener 9 dígitos');
      return;
    }
    if (!this.inquilinoForm.codcuar) {
      alert('Seleccione un cuarto');
      return;
    }

    const payload = {
      nominq: this.inquilinoForm.nombres,
      apepinq: this.inquilinoForm.apePaterno,
      apeminq: this.inquilinoForm.apeMaterno,
      docinq: this.dniBusqueda,
      celinq: this.inquilinoForm.celular,
      corinq: this.inquilinoForm.correo,
      fechaInicio: this.inquilinoForm.fechaInicio,
      codcuar: Number(this.inquilinoForm.codcuar),
      codusu: this.usuario.codusu
    };

    this.inquilinoService.registrar(payload).subscribe({
      next: () => {
        this.mostrarAlertaExitoInquilino = true;
        this.cdr.detectChanges();
        setTimeout(() => {
          this.mostrarAlertaExitoInquilino = false;
          this.cerrarModalInquilino();
          this.refrescarVistaActual();
          this.subModo = 'inquilinos';
        },2500);
      },
      error:(err)=>{
        console.error(err);
        let mensaje="Error al registrar";
        if(err.error){
          mensaje=err.error;
        }
        alert(mensaje);
      }
    });
  }

  togglePanelMoverCuarto(): void {
    this.mostrarPanelMoverCuarto = !this.mostrarPanelMoverCuarto;
  }

  cancelarCambioCuarto(): void {
    this.codSedeNueva = this.codSedeOriginal;
    this.codPisoNueva = this.codPisoOriginal;
    this.codCuartoNueva = this.codCuartoOriginal;
    this.mostrarPanelMoverCuarto = false;
  }

  irEditarInquilino(item:any): void {
    this.inquilinoService.obtener(item.codasig).subscribe({
      next: data => {
        this.codInquilinoSeleccionado = data.codinq;
        this.codAsignacionSeleccionada = data.codasig;
        this.dniBusqueda = data.docinq;

        this.inquilinoForm = {
          codinq: data.codinq,
          codasig: data.codasig,
          dni: data.docinq,
          nombres: data.nominq,
          apePaterno: data.apepinq,
          apeMaterno: data.apeminq,
          celular: data.celinq,
          correo: data.corinq,
          fechaInicio: data.fechaInicio,
          codcuar: data.codcuar,
          codsede: data.codsede,
          codpiso: data.codpiso,
          numeroPiso: data.numeroPiso,
          numeroCuarto: data.numeroCuarto,
          nombreSede: data.nombreSede
        };

        this.codSedeOriginal = data.codsede;
        this.codPisoOriginal = data.codpiso;
        this.codCuartoOriginal = data.codcuar;
        this.codSedeNueva = data.codsede;

        this.cargarPisosMover(data.codpiso, data.codcuar);
        this.mostrarModalEditarInquilino = true;
        this.cdr.detectChanges();
      },
      error: err => console.error(err)
    });
  }

  finalizarContrato(i: any): void {
    if (!confirm(`¿Finalizar el contrato de ${i.nombreCompleto}?`)) {
      return;
    }
    this.inquilinoService.finalizarContrato(i.codasig).subscribe({
      next: () => {
        this.refrescarVistaActual();
      },
      error: err => console.error(err)
    });
  }

  reanudarContrato(i: any): void {
    this.inquilinoService.reanudarContrato(i.codasig).subscribe({
      next: () => {
        alert("Contrato reanudado correctamente.");
        this.cargarContratosFinalizados();
        this.refrescarVistaActual();
      },
      error: err => {
        alert(err.error);
      }
    });
  }

  abrirModalContratosFinalizados(): void{
    this.mostrarModalContratosFinalizados = true;
    this.cargarContratosFinalizados();
  }

  cerrarModalContratosFinalizados(): void{
    this.mostrarModalContratosFinalizados = false;
  }

  cargarContratosFinalizados(): void{
    this.inquilinoService.listarContratosFinalizados(this.usuario.codusu).subscribe({
      next:data=>{
        this.contratosFinalizados=data;
        this.cdr.detectChanges();
      },
      error:err=>console.error(err)
    });
  }

  abrirModalEditarInquilino(){
    this.mostrarModalEditarInquilino = true;
  }

  cerrarModalEditarInquilino(){
    this.mostrarModalEditarInquilino = false;
    this.mostrarPanelMoverCuarto = false;
  }

  guardarCambiosInquilino(): void {
    if (this.codInquilinoSeleccionado == null) {
      return;
    }
    const cuartoDestino = this.codCuartoNueva ?? this.inquilinoForm.codcuar;
    
    const payload = {
      codasig: this.codAsignacionSeleccionada,
      nominq: this.inquilinoForm.nombres,
      apepinq: this.inquilinoForm.apePaterno,
      apeminq: this.inquilinoForm.apeMaterno,
      docinq: this.dniBusqueda,
      celinq: this.inquilinoForm.celular,
      corinq: this.inquilinoForm.correo,
      fechaInicio: this.inquilinoForm.fechaInicio,
      codcuar: cuartoDestino,
      codusu: this.usuario.codusu
    };

    this.inquilinoService.actualizar(this.codInquilinoSeleccionado!, payload).subscribe({
      next: () => {
        alert("Datos actualizados correctamente.");
        this.refrescarVistaActual();
        this.cdr.detectChanges();
        this.cerrarModalEditarInquilino();
      },
      error: err => {
        console.error(err);
        alert(err.error);
      }
    });
  }

  cargarPisosMover(pisoSeleccionado?: number, cuartoSeleccionado?: number): void {
    this.pisosDisponiblesMover = [];
    this.cuartosDisponiblesMover = [];

    if (!this.codSedeNueva) {
      return;
    }

    this.pisoService.listarPorSede(this.codSedeNueva).subscribe({
      next: data => {
        this.pisosDisponiblesMover = data;
        if(pisoSeleccionado) {
          this.codPisoNueva = pisoSeleccionado;
          this.cargarCuartosMover(cuartoSeleccionado);
        }
      },
      error: err => console.error(err)
    });
  }

  cargarCuartosMover(cuartoSeleccionado?: number): void {
    this.cuartosDisponiblesMover = [];
    if (!this.codPisoNueva) {
      return;
    }

    this.cuartoService.listarPorPiso(this.codPisoNueva).subscribe({
      next: data => {
        this.cuartosDisponiblesMover = data;
        if(cuartoSeleccionado){
          this.codCuartoNueva = cuartoSeleccionado;
        }
        this.cdr.detectChanges();
      },
      error: err => console.error(err)
    });
    console.log(this.cuartosDisponiblesMover);
  }

  cambioSedeMover(): void {
    this.codPisoNueva = null;
    this.codCuartoNueva = null;
    this.pisosDisponiblesMover = [];
    this.cuartosDisponiblesMover = [];
    this.cargarPisosMover();
  }

  cambioPisoMover(): void {
    this.codCuartoNueva = null;
    this.cuartosDisponiblesMover = [];
    this.cargarCuartosMover();
  }

  // =========================================================
  // 7. DOCUMENTOS
  // =========================================================
  abrirDocumento(i: any): void {
    this.inquilinoSeleccionado = {
      codinq: i.codinq ?? i.codInq ?? i.id,
      codusu: this.usuario.codusu,
      nombreCompleto: i.nombreCompleto
    };
    this.mostrarDocumentos = true;
    this.cdr.detectChanges();
  }

  cerrarDocumentos(): void {
    this.mostrarDocumentos = false;
    this.inquilinoSeleccionado = null;
  }

  verDocumentosInquilino(inq: any): void {
    console.log('Visualización de documentos sin acción para el inquilino:', inq.nominq);
  }

  guardarSede(formulario: NgForm) {
    this.mostrarErroresSede = true;

    if (
      formulario.invalid ||
      this.sedeForm.cantidadPisos < 1 ||
      this.sedeForm.cantidadPisos > 30 ||
      this.sedeForm.cuartosPorPiso.some((c: number) => !c || c < 1 || c > 10)
    ) {
      Object.values(formulario.controls).forEach((control) => {
        control.markAsTouched();
      });
      return;
    }

    this.sedeForm.codusu = this.usuario.codusu;
    console.log("Datos listos para enviar al backend:", this.sedeForm);
    
    this.sedeService.crearMasiva(this.sedeForm).subscribe({
      next: (respuesta) => {
          console.log('Sede guardada con éxito:', respuesta);
          this.listarSedes();
          this.sedeForm = {
            nombre: '',
            direccion: '',
            descripcion: '',
            cantidadPisos: 1,
            cuartosPorPiso: [1],
            cuartosGlobal: null,
            codusu: this.usuario.codusu,
            precioCuarto: 0
          };
          this.cerrarModalSede();
      },
      error: (err) => {
          console.error('Error al guardar la sede:', err);
          alert("Hubo un error en el servidor al procesar la sede masiva.");
      }
    });
  }

  // =========================================================
  // 8. FIX
  // =========================================================
  trackByIndex(index: number): number {
    return index;
  }

  validarMaxPisos(event: any): void {
    let valor = event.target.value.replace(/[^0-9]/g, '');
    if (!valor) {
      this.sedeForm.cantidadPisos = null;
      return;
    }
    let numero = parseInt(valor, 10);
    if (numero > 30) {
      numero = 30;
    }
    this.sedeForm.cantidadPisos = numero;
    event.target.value = numero;
  }

  validarMaxCuartosGlobal(event: any): void {
    let valor = event.target.value.replace(/[^0-9]/g, '');
    if (!valor) {
      this.sedeForm.cuartosGlobal = null;
      return;
    }
    let numero = parseInt(valor, 10);
    if (numero > 10) {
      numero = 10;
    }
    this.sedeForm.cuartosGlobal = numero;
    event.target.value = numero;
  }

  validarMaxCuartosPiso(event: any, index: number): void {
    let valor = event.target.value.replace(/[^0-9]/g, '');
    if (!valor) {
      this.sedeForm.cuartosPorPiso[index] = null;
      return;
    }
    let numero = parseInt(valor, 10);
    if (numero > 10) {
      numero = 10;
    }
    this.sedeForm.cuartosPorPiso[index] = numero;
    event.target.value = numero;
  }

  abrirServicio(): void {
    this.mostrarGestionServicio = true;
  }

  cerrarServicio(): void {
    this.mostrarGestionServicio = false;
  }

  // =========================================================
  // 9. RESERVAS
  // =========================================================
  abrirModalEditarReserva(reserva: any): void {
    console.log('Abriendo edición para reserva:', reserva);
  }

  verDetallesPago(reserva: any): void {
    console.log('Mostrando pagos de la reserva:', reserva);
  }

  cancelarReserva(reserva: any): void {
    if (confirm(`¿Estás seguro de que deseas cancelar la reserva #${reserva.idReserva}?`)) {
      console.log('Cancelando:', reserva);
      reserva.estadoReserva = 'Cancelada';
      this.cdr.detectChanges();
    }
  }

  confirmarReserva(reserva: any): void {
    console.log('Confirmando reserva:', reserva);
    reserva.estadoReserva = 'Confirmada';
    this.cdr.detectChanges();
  }

  // =========================================================
  // 10. METRICAS
  // =========================================================
  mostrarModalMetricas = false;
  
  abrirMetricas(){
    this.mostrarModalMetricas = true;
  }

  cerrarMetricas(){
    this.mostrarModalMetricas = false;
  }

  cargarMetricas(): void {
    this.metricasService.dashboard(this.usuario.codusu).subscribe({
      next: data => {
        this.metricas = data;
        this.cdr.detectChanges();
      },
      error: err => console.error(err)
    });
  }

  // =========================================================
  // 11. EXTENDER MES (NUEVO)
  // =========================================================
  extenderMes(inquilino: any): void {
  if (!inquilino.codasig) {
    alert('Error: Este inquilino no tiene un código de asignación (codasig) registrado.');
    return;
  }

  const confirmar = confirm(
    `¿Estás seguro de extender la fecha de pago de ${inquilino.nombreCompleto} por UN (1) mes?\n\n` +
    `Se registrará un pago por TRANSFERENCIA y se extenderá la fecha de vencimiento.`
  );
  
  if (confirmar) {
    const usuario = JSON.parse(localStorage.getItem('usuario') || '{}');
    
    this.http.put(
      `http://localhost:8080/api/inquilino-cuarto/extender-mes/${inquilino.codasig}?codusu=${usuario.codusu}`, 
      {},
      { responseType: 'text' }  // <-- AGREGA ESTO: Indica que esperas texto, no JSON
    ).subscribe({
      next: (response) => {
        alert('✅ Fecha extendida y pago registrado correctamente.\n\n' +
              'Se ha creado un registro de pago por transferencia en el sistema.');
        
        if (inquilino.fechout) {
          const fechaActual = new Date(inquilino.fechout);
          fechaActual.setMonth(fechaActual.getMonth() + 1);
          inquilino.fechout = fechaActual.toISOString().split('T')[0];
          
          const hoy = new Date();
          const diferenciaTiempo = fechaActual.getTime() - hoy.getTime();
          inquilino.diasRestantes = Math.ceil(diferenciaTiempo / (1000 * 3600 * 24));
        }
        
        this.cargarMetricas();
        this.refrescarVistaActual();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al extender mes:', err);
        // Solo muestra error si realmente hubo un problema en el backend
        if (err.status !== 200) {
          alert('❌ Ocurrió un error al intentar extender la fecha y registrar el pago.');
        }
      }
    });
  }
}
  abrirModalHistoricos(): void {
    this.mostrarModalHistoricos = true;
  }

  cerrarModalHistoricos(): void {
    this.mostrarModalHistoricos = false;
  }
}
