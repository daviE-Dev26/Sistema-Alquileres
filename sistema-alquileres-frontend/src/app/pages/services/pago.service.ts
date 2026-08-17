import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PagoService {
  private api = 'http://localhost:8080/pagos';
  constructor(
    private http: HttpClient
  ) {}
  confirmarPago(body: any): Observable<any> {

    return this.http.post(
      `${this.api}/confirmar`,
      body
    );
  }
}