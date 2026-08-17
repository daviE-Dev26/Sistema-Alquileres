import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PagoService {

  private api = 'http://localhost:8080/pagos';

  constructor(private http: HttpClient) { }

  obtenerProximosPagos(codinq: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/proximos-pagos/${codinq}`);
  }
}