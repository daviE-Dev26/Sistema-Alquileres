package com.SistemaAlquiler.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResumenPisoDTO {

    private Integer codpiso;
    private Integer numero;
    private Boolean estado;
    private Integer cantidadCuartos;
    private Integer cantidadInquilinos;
    private String nombreSede;
    private String direccionSede;
}