import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ServicioService {

  private api = 'http://localhost:8080/servicio';

  constructor(private http: HttpClient) { }

  listarServicios(codusu: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/usuario/${codusu}`);
  }

  listarPorTipo(codusu: number, tipo: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/usuario/${codusu}/tipo/${tipo}`);
  }

  crear(servicio: any): Observable<any> {
    return this.http.post(`${this.api}/create`, servicio);
  }

  actualizar(codserv: number, servicio: any): Observable<any> {
    return this.http.put(`${this.api}/update/${codserv}`, servicio);
  }

  listarSedes(codusu: number): Observable<any[]> {
    return this.http.get<any[]>(`http://localhost:8080/sede/usuario/${codusu}`);
  }
  listarPorSede(codusu: number, codsede: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/usuario/${codusu}/sede/${codsede}`);
  }
}