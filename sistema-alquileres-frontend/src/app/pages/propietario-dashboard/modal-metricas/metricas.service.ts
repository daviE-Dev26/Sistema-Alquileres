import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MetricasService {

  private url = 'http://localhost:8080/metricas';

  constructor(private http: HttpClient) { }

  dashboard(codusu:number):Observable<any>{

      return this.http.get<any>(
        `${this.url}/dashboard/${codusu}`
      );

  }
  dashboardPorSede(
codusu:number,
codsede:number
){

return this.http.get<any>(

`${this.url}/dashboard/${codusu}/sede/${codsede}`

);

}
}