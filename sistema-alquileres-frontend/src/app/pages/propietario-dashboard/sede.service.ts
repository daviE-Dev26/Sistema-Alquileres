import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SedeService {

  private api =
    'http://localhost:8080/sede';

  constructor(
    private http: HttpClient
  ) {}

  listarPorUsuario(
    codusu: number
  ): Observable<any[]> {

    return this.http.get<any[]>(
      `${this.api}/resumen/usuario/${codusu}`
    );
  }

  crear(
    sede: any
  ): Observable<any> {

    return this.http.post(
      this.api,
      sede
    );
  }

  crearMasiva(
    sede: any
  ): Observable<any> {

    return this.http.post(
      `${this.api}/masiva`,
      sede
    );
  }

  habilitar(
    codsede: number
  ): Observable<any> {

    return this.http.put(
      `${this.api}/habilitar/${codsede}`,
      {}
    );
  }

  deshabilitar(
    codsede: number
  ): Observable<any> {

    return this.http.put(
      `${this.api}/deshabilitar/${codsede}`,
      {}
    );
  }
  
}