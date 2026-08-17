package com.SistemaAlquiler.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class PagoHistorialDTO{

private Integer codpago;
private String inquilino;
private String cuarto;
private Double monto;
private Integer cantidadMeses;
private LocalDate periodoInicio;
private LocalDate periodoFin;
private String metodoPago;
private String estadoPago;
private LocalDateTime fechaPago;
private String observacion;
}