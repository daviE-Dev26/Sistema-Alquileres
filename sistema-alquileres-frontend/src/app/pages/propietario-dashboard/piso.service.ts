import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PisoService {

  private url = 'http://localhost:8080/piso';

  constructor(private http: HttpClient) {}

  listarPorSede(codsede: number): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.url}/sede/${codsede}`
    );
  }
listarResumenPorSede(codsede: number) {
  return this.http.get<any[]>(
    `${this.url}/resumen/sede/${codsede}`
  );
}
listarTodosLosPisos(codusu: number) {

  return this.http.get<any[]>(
    `${this.url}/usuario/${codusu}`
  );

}
}
