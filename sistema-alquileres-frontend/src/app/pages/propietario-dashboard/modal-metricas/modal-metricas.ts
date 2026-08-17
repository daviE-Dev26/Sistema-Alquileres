import {
Component,
EventEmitter,
Output,
OnInit
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MetricasService } from './metricas.service';
import { ChangeDetectorRef } from '@angular/core';
import { Chart, registerables } from 'chart.js';
import {ViewChild,ElementRef} from '@angular/core';
import { SedeService } from '../sede.service';
Chart.register(...registerables);
@Component({

selector:'app-modal-metricas',

standalone:true,

imports:[
CommonModule,
FormsModule
],

templateUrl:'./modal-metricas.html',

styleUrls:['./modal-metricas.css']

})

export class ModalMetricas implements OnInit{

@Output()

cerrarModal =
new EventEmitter<void>();

metricas:any;

rentabilidad:number=0;

// NUEVO
graficoFinanzas!: Chart;

graficoOcupacion!: Chart;

sedes:any[]=[];

sedeSeleccionada:number|null=null;

@ViewChild('graficoFinanzas')
graficoFinanzasCanvas!: ElementRef;

@ViewChild('graficoOcupacion')
graficoOcupacionCanvas!: ElementRef;
constructor(
private sedeService:SedeService,
private metricasService:MetricasService,
private cdr: ChangeDetectorRef
){}

usuario:any;

ngOnInit():void{
this.usuario =
JSON.parse(
localStorage.getItem('usuario') || '{}'
);
this.cargarSedes();
this.cargarMetricas();
}

cargarMetricas(){

let peticion;


if(this.sedeSeleccionada){

peticion =
this.metricasService
.dashboardPorSede(
this.usuario.codusu,
this.sedeSeleccionada
);

}else{


peticion =
this.metricasService
.dashboard(
this.usuario.codusu
);

}


peticion.subscribe({

next:data=>{

this.metricas=data;

this.calcularRentabilidad();

setTimeout(()=>{

    this.crearGraficos();

});

}

});


}

calcularRentabilidad():void{
if(!this.metricas){
return;
}
if(this.metricas.ingresos==0){
this.rentabilidad=0;
return;
}
this.rentabilidad=Math.round(
(this.metricas.ganancia/
this.metricas.ingresos)*100
);
  this.cdr.detectChanges();
}

cerrar(){
this.cerrarModal.emit();
}

crearGraficos():void{
if(this.graficoFinanzas){
this.graficoFinanzas.destroy();
}
if(this.graficoOcupacion){
this.graficoOcupacion.destroy();
}
// ===========================
// GRAFICO FINANCIERO
// ===========================
this.graficoFinanzas = new Chart(
this.graficoFinanzasCanvas.nativeElement,
{
type:'bar',
data:{
labels:[
'Ingresos',
'Gastos',
'Ganancia'
],
datasets:[{
label:'Soles',
data:[
this.metricas.ingresos,
this.metricas.gastos,
this.metricas.ganancia
]
}]
},
options:{
responsive:true,
maintainAspectRatio:false
}
}
);
// ===========================
// GRAFICO OCUPACION
// ===========================
this.graficoOcupacion = new Chart(
this.graficoOcupacionCanvas.nativeElement,
{
type:'doughnut',
data:{
labels:[
'Ocupados',
'Disponibles'
],
datasets:[{
data:[
this.metricas.ocupados,
this.metricas.disponibles
]
}]
},
options:{
responsive:true,
maintainAspectRatio:false
}
}
);
}

cargarSedes(){

this.sedeService
.listarPorUsuario(this.usuario.codusu)
.subscribe({

next:data=>{

this.sedes=data;
this.cdr.detectChanges();
}

});

}
cambioSede(){
    this.cargarMetricas();
}
}
