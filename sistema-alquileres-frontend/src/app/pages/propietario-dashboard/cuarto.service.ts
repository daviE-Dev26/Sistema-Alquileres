import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CuartoService {

  // Tu variable original se llama 'url'
  private url = 'http://localhost:8080/cuarto';

  constructor(private http: HttpClient) { }

  // ✅ CORREGIDO: Se cambió 'this.apiUrl' por 'this.url'
  listarCuartos(codusu: number): Observable<any[]> {
  // ✅ Corregido: cambiamos 'apiUrl' por 'url'
  return this.http.get<any[]>(`${this.url}?codusu=${codusu}`);
}

  crearCuarto(data: any): Observable<any> {
    return this.http.post(this.url, data);
  }

  actualizarCuarto(id: number, data: any): Observable<any> {
    return this.http.put(`${this.url}/${id}`, data);
  }

  cambiarEstado(id: number, estado: string): Observable<any> {
    return this.http.put(`${this.url}/${id}/estado?estado=${estado}`, {});
  }

  // ✅ CORREGIDO: Ahora recibe el codusu para que Spring Boot filtre las propiedades disponibles
  listarDisponibles(codusu: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.url}/disponibles?codusu=${codusu}`);
  }
  listarPorSede(codsede: number): Observable<any[]> {

  return this.http.get<any[]>(
    `${this.url}/sede/${codsede}`
  );
}
listarPorPiso(codpiso: number){

  return this.http.get<any[]>(
    `${this.url}/piso/${codpiso}`
  );

}
listarDisponiblesPorPiso(codpiso:number): Observable<any[]> {

  return this.http.get<any[]>(
    `${this.url}/disponibles/piso/${codpiso}`
  );

}
obtenerPorId(id: number): Observable<any> {
  return this.http.get<any>(`${this.url}/${id}`);
}
cambiarHabilitado(id: number, estado: boolean): Observable<any> {
  return this.http.put(`${this.url}/${id}/habilitado?estado=${estado}`, {});
}
findById(id: number): Observable<any> {
  return this.http.get<any>(`${this.url}/${id}`);
}
}