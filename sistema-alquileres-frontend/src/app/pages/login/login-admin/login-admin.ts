import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink,Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-login-admin',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login-admin.html',
  styleUrls: ['./login-admin.css']
})
export class LoginAdmin {

  correo = '';
  password = '';
  error = false;
  mensaje = '';

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  login() {
    this.error = false;
    this.mensaje = '';

    this.http.post<any>(
      'http://localhost:8080/usuario/login',
      {
        correo: this.correo,
        password: this.password,
        codrol: 1 // 
      }
    ).subscribe({
      next: (res) => {
        console.log(res);

        // Evaluamos la propiedad que devuelve tu backend (asumiendo que mapea a true/false el éxito)
        if (res.success) {
          
          localStorage.setItem(
            'usuario',
            JSON.stringify(res)
          );

          alert('Bienvenido Administrador ' + res.nombre);
          this.router.navigate(['/admin-dashboard']);

        } else {
          // Captura los errores controlados (Usuario no existe, Password incorrecta, No tienes permisos)
          this.error = true;
          this.mensaje = res.message;
          alert(res.message);
        }
      },
      error: (err) => {
        console.log(err);
        this.error = true;
        this.mensaje = 'Error de conexión con el servidor';
        alert('Error de conexión con el servidor');
      }
    });
  }
}