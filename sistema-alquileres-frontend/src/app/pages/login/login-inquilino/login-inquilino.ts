import { Component, ChangeDetectorRef } from '@angular/core'; // 👈 Importa ChangeDetectorRef
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { InquilinoService } from '../../propietario-dashboard/inquilino.service';

@Component({
  selector: 'app-login-inquilino',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login-inquilino.html',
  styleUrls: ['./login-inquilino.css']
})
export class LoginInquilino {
  codigoAcceso: string = '';
  errorMessage: string = '';

  constructor(
    private inquilinoService: InquilinoService, 
    private router: Router,
    private cdr: ChangeDetectorRef // 👈 Inyéctalo aquí en el constructor
  ) {}

  acceder() {    
    this.errorMessage = '';

    if (!this.codigoAcceso || !this.codigoAcceso.trim()) {
      this.errorMessage = 'Por favor, ingresa tu código de acceso.';
      return;
    }

    this.inquilinoService.loginPorCodigo(this.codigoAcceso.trim()).subscribe({
      next: (loginData) => {
        localStorage.setItem('loginData', JSON.stringify(loginData));
        this.router.navigate(['/inquilino-dashboard']);
      },
      error: (err) => {
        // Asignamos el mensaje que viene desde el backend
        this.errorMessage = typeof err.error === 'string' ? err.error : 'Código incorrecto o cuarto no ocupado.';
        
        // 🚀 FORZAMOS LA DETECCIÓN DE CAMBIOS AQUÍ:
        // Esto obliga a Angular a pintar el mensaje en el HTML al instante sin esperar otra acción
        this.cdr.detectChanges(); 
      }
    });
  }
}