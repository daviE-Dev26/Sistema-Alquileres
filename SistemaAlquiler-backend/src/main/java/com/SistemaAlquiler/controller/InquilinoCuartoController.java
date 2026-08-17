package com.SistemaAlquiler.controller;

import com.SistemaAlquiler.service.InquilinoCuartoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inquilino-cuarto")
@CrossOrigin(origins = "*")
public class InquilinoCuartoController {

    @Autowired
    private InquilinoCuartoService inquilinoCuartoService;

    @PutMapping("/extender-mes/{codasig}")
    public ResponseEntity<String> extenderMes(
            @PathVariable Integer codasig,
            @RequestParam Integer codusu) {
        try {
            inquilinoCuartoService.extenderMesConPago(codasig, codusu);
            return ResponseEntity.ok("Fecha extendida y pago registrado correctamente por transferencia.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error al extender la fecha y registrar el pago: " + e.getMessage());
        }
    }
}