import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
declare var Culqi: any;
declare global {
  interface Window {
    culqi: () => void;
  }
}
@Injectable({
  providedIn: 'root'
})
export class CulqiService {

  constructor() {}

  abrirCheckout(
    monto: number,
    email: string,
    callback: (token: string) => void
  ) {

    Culqi.publicKey = environment.culqiPublicKey;

    Culqi.settings({
      title: 'Sistema de Alquileres',
      currency: 'PEN',
      amount: monto * 100,
      description: 'Pago de alquiler'
    });

    Culqi.options({
      lang: 'es'
    });
console.log(Culqi);
    Culqi.open();

    window.culqi = () => {

      if (Culqi.token) {

        callback(Culqi.token.id);

      } else {

        console.error(Culqi.error);

      }

    };

  }

}