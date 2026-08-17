package com.SistemaAlquiler.dto;

import java.time.LocalDate;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarPagoDTO {
    private Integer codasig;
    private Integer cantidadMeses;
    private LocalDate periodoInicio;
    private String metodoPago;
    private String observacion;
    private String origenPago;
}