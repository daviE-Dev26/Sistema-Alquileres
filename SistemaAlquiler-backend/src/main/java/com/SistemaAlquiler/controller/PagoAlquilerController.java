package com.SistemaAlquiler.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SistemaAlquiler.dto.ConfirmarPagoDTO;
import com.SistemaAlquiler.dto.EstadoCuentaDTO;
import com.SistemaAlquiler.dto.PagoHistorialDTO;
import com.SistemaAlquiler.service.PagoAlquilerService;

@RestController
@RequestMapping("/pagos")
@CrossOrigin("*")
public class PagoAlquilerController {


@Autowired
private PagoAlquilerService pagoService;


@GetMapping("/historial/{codasig}")
public List<PagoHistorialDTO> historial(
@PathVariable Integer codasig){

return pagoService.historialPago(codasig);

}
@GetMapping("/estado-cuenta/{codasig}")
public EstadoCuentaDTO estadoCuenta(
@PathVariable Integer codasig){
return pagoService.obtenerEstadoCuenta(codasig);
}
@PostMapping("/confirmar")
public ResponseEntity<?> confirmarPago(
@RequestBody ConfirmarPagoDTO dto){

return ResponseEntity.ok(
		pagoService.confirmarPago(dto)
);
}
@GetMapping("/proximos-pagos/{codinq}")
public ResponseEntity<List<Map<String, Object>>> obtenerProximosPagos(
        @PathVariable Integer codinq) {
    try {
        List<Map<String, Object>> pagos = pagoService.obtenerPagosPorInquilino(codinq);
        return ResponseEntity.ok(pagos);
    } catch (Exception e) {
        return ResponseEntity.badRequest().build();
    }
}
}
