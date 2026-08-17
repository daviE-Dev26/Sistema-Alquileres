import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink,Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
selector:'app-registro-propietario',
standalone:true,
imports:[CommonModule,FormsModule,RouterLink],
templateUrl:'./registro-propietario.html',
styleUrls:['./registro-propietario.css']
})
export class RegistroPropietario{

nomusu='';
apepusu='';
apemusu='';
docusu='';
dirusu='';
celusu='';
corusu='';
password='';

codrol=2;
codtipdoc=1;

mensaje='';
error=false;

constructor(private http:HttpClient,private router:Router){}

registrar(){

this.error=false;
this.mensaje='';

const soloTexto=/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/;
const soloNumeros=/^[0-9]+$/;
const correoValido=/^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const passwordSegura=/^(?=.*[A-Z])(?=.*[a-z])(?=.*\d).{8,}$/;

if(
this.nomusu.trim()===''||
this.apepusu.trim()===''||
this.apemusu.trim()===''||
this.docusu.trim()===''||
this.dirusu.trim()===''||
this.celusu.trim()===''||
this.corusu.trim()===''||
this.password.trim()===''
){
alert('Todos los campos son obligatorios');
return;
}

if(!soloTexto.test(this.nomusu)){
alert('El nombre no debe contener números');
return;
}

if(!soloTexto.test(this.apepusu)){
alert('El apellido paterno no debe contener números');
return;
}

if(!soloTexto.test(this.apemusu)){
alert('El apellido materno no debe contener números');
return;
}

if(
!soloNumeros.test(this.docusu)||
this.docusu.length!==8
){
alert('El DNI debe tener exactamente 8 dígitos');
return;
}

if(this.docusu.startsWith('000')){
alert('El DNI no puede iniciar con 000');
return;
}

if(
!soloNumeros.test(this.celusu)||
this.celusu.length!==9
){
alert('El celular debe tener 9 dígitos');
return;
}

if(!this.celusu.startsWith('9')){
alert('El celular debe iniciar con 9');
return;
}

if(!correoValido.test(this.corusu)){
alert('Correo electrónico inválido');
return;
}

if(!passwordSegura.test(this.password)){
alert(
'La contraseña debe tener mínimo 8 caracteres, una mayúscula, una minúscula y un número'
);
return;
}

const body={

nomusu:this.nomusu.trim(),
apepusu:this.apepusu.trim(),
apemusu:this.apemusu.trim(),
docusu:this.docusu.trim(),
dirusu:this.dirusu.trim(),
celusu:this.celusu.trim(),
corusu:this.corusu.trim(),
password:this.password,
codrol:this.codrol,
codtipdoc:this.codtipdoc

};

this.http.post(
'http://localhost:8080/usuario',
body
).subscribe({

next:()=>{

alert('Solicitud enviada correctamente');

setTimeout(()=>{

this.router.navigate(['/login/propietario']);

},1000);

},

error:()=>{

alert('Error al enviar solicitud');

}

});

}
}