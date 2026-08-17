import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
providedIn: 'root'
})
export class ReniecService {

private token = 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6Im1lbmF6b2xvbDQ1QGdtYWlsLmNvbSJ9.be45euWJuIEB-21wpA2cX6lbkbz1G2UAB0_yDepcSHI';

constructor(private http: HttpClient) { }

consultarDni(dni: string): Observable<any> {


const url =
`https://dniruc.apisperu.com/api/v1/dni/${dni}?token=${this.token}`;

return this.http.get(url);
}
}
