import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class InquilinoService {

  private url = 'http://localhost:8080/inquilino';

  constructor(private http: HttpClient) {}
  // 🔐 NUEVO: Envía el código 'passcuar' al backend para autenticar al inquilino
 loginPorCodigo(codigo: string): Observable<any> {
  return this.http.post<any>('http://localhost:8080/cuarto/login-acceso', { codigo });
}
  registrar(data: any): Observable<any> {
    return this.http.post(this.url, data);
  }

  listar(): Observable<any[]> {
    return this.http.get<any[]>(this.url);
  }

  listarDashboard(codusu: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.url}/dashboard?codusu=${codusu}`);
  }

  // 📝 NUEVO: Envía los datos editados al backend usando PUT pasando el ID en la URL
  actualizar(id: number, data: any): Observable<any> {
    return this.http.put(`${this.url}/${id}`, data);
  }

finalizarContrato(codasig:number){

    return this.http.put(
        `${this.url}/${codasig}/finalizar`,
        {}
    );

}
reanudarContrato(codasig: number): Observable<any> {
  return this.http.put(
    `${this.url}/${codasig}/reanudar`,
    {}
  );
}
listarPorSede(codsede: number) {
  return this.http.get<any[]>(
    `${this.url}/sede/${codsede}`
  );
}

listarPorPiso(codpiso: number) {
  return this.http.get<any[]>(
    `${this.url}/piso/${codpiso}`
  );
}
  listarPorCuarto(codcuar: number) {

  return this.http.get<any[]>(
    `${this.url}/cuarto/${codcuar}`
  );
}
listarContratosFinalizados(codusu:number):Observable<any>{

    return this.http.get<any>(
        `${this.url}/contratos-finalizados?codusu=${codusu}`
    );

}
obtener(codasig:number){

  return this.http.get<any>(
      `${this.url}/${codasig}`
  );

}
  getReservas(codusu: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.url}/dashboard/reservas/${codusu}`);
  }
  actualizarContactoPortal(codinq: number, data: any) {
    return this.http.put<any>(
      `${this.url}/${codinq}/contacto`,
      data
    );
  }
    obtenerHistoricoDatos(
    dni: string,
    codusu: number
  ): Observable<any[]> {

    return this.http.get<any[]>(
      `${this.url}/historico-datos`,
      {
        params: {
          dni: dni,
          codusu: codusu.toString()
        }
      }
    );

  }
  obtenerHistoricoCuartos(
    dni: string,
    codusu: number
  ): Observable<any[]> {

    return this.http.get<any[]>(
      `${this.url}/historico-cuartos`,
      {
        params: {
          dni: dni,
          codusu: codusu.toString()
        }
      }
    );

  }
  obtenerTodosHistoricoDatos(codusu: number) {
    return this.http.get<any[]>(
      `${this.url}/historico-datos/todos`,
      {
        params: {
          codusu: codusu
        }
      }
    );
  }

  obtenerTodosHistoricoCuartos(codusu: number) {
    return this.http.get<any[]>(
      `${this.url}/historico-cuartos/todos`,
      {
        params: {
          codusu: codusu
        }
      }
    );
  }
}