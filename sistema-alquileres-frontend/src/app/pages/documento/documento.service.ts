import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DocumentoService {

  private api = 'http://localhost:8080/documentos';

  constructor(private http: HttpClient) { }

  listarPorInquilino(codinq: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/inquilino/${codinq}`);
  }

  crear(formData: FormData): Observable<any> {
    return this.http.post(`${this.api}/create`, formData);
  }

  actualizar(id: number, formData: FormData): Observable<any> {
    return this.http.put(`${this.api}/update/${id}`, formData);
  }

  deshabilitar(id: number): Observable<any> {
    return this.http.put(`${this.api}/disable/${id}`, {});
  }

  habilitar(id: number): Observable<any> {
    return this.http.put(`${this.api}/enable/${id}`, {});
  }

  restaurar(id: number): Observable<any> {
    return this.http.put(`${this.api}/restore/${id}`, {})
  }

  listarPorTipo(codinq: number, tipdoc?: string): Observable<any[]> {
    let url = `${this.api}/inquilino/${codinq}/tipo`;

    if (tipdoc) {
      url += `?tipdoc=${tipdoc}`;
    }

    return this.http.get<any[]>(url);
  }
  listarFiltrado(codinq: number, tipdoc?: string, soloActivos?: boolean): Observable<any[]> {

    let url = `${this.api}/inquilino/${codinq}/filtro?`;

    if (tipdoc) {
      url += `tipdoc=${tipdoc}&`;
    }

    if (soloActivos) {
      url += `activos=true`;
    }

    return this.http.get<any[]>(url);
  }


}