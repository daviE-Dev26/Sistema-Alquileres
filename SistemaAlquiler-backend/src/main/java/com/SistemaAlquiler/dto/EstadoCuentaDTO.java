package com.SistemaAlquiler.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class EstadoCuentaDTO {
    private String inquilino;
    private String cuarto;
    private Double montoMensual;
    private Double deudaActual;
    private Integer mesesPagados;
    private Integer mesesPendientes;
    private LocalDate ultimoPago;
    private LocalDate proximoPago;
    private List<DetallePeriodoDTO> periodos;

}