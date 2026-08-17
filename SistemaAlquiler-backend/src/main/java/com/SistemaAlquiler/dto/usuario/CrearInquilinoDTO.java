package com.SistemaAlquiler.dto.usuario;
import java.time.LocalDate;

import lombok.Data;

@Data
public class CrearInquilinoDTO {
	private Integer codinq;
    private String nominq;
    private String apepinq;
    private String apeminq;
    private String docinq;
    private String celinq;
    private String corinq;

    private Integer codcuar;
    private Integer codusu;

    private LocalDate fechaInicio;

    private Integer codasig;

    private Integer codsede;

    private Integer codpiso;
    private Integer numeroPiso;
    private Integer numeroCuarto;

    private String nombreSede;
    private String estado;
}