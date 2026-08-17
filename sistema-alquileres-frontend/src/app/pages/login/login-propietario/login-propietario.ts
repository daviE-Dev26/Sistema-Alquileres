import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink,Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-login-propietario',
  standalone:true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login-propietario.html',
  styleUrl: './login-propietario.css',
})
export class LoginPropietario {

  correo='';
  password='';

  constructor(private http: HttpClient, private router:Router){}

  login(){

    const data={
        correo: this.correo,
        password: this.password,
        codrol: 1 
      }

    this.http.post<any>('http://localhost:8080/usuario/login',data)
    .subscribe({
      
      next:(response)=>{
        if(!response.success){
          alert(response.message);
          return;
        }


        localStorage.setItem('usuario',JSON.stringify(response));

        if(response.codrol ===1){
          this.router.navigate(['/propietario-dashboard']);
        }
      },
      error:()=>{
        alert('Error en el servidor');
      }
    });   
  }
}