package com.SistemaAlquiler.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class ContratoFinalizadoDTO {
	private Integer codasig;
    private Integer codcuar;
    private String nombreCompleto;
    private String nombreSede;
    private Integer numeroPiso;
    private Integer numCuarto;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

}