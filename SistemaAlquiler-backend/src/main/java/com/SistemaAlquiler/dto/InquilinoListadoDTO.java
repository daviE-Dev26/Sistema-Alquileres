package com.SistemaAlquiler.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InquilinoListadoDTO {

    private Integer codasig;
    private Integer codinq;
    private String nombreCompleto;
    private String dni;
    private String celular;
    private String correo;
    private Integer numCuarto;
    private Long diasRestantes;
    private String estado;
    
    private String nombreSede;
    private Integer numeroPiso;
}