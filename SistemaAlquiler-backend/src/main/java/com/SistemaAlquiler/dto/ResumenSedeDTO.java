package com.SistemaAlquiler.dto;

import lombok.Data;

@Data
public class ResumenSedeDTO {

    private Integer codsede;
    private String nombre;
    private String direccion;
    private String descripcion;
    private Integer cantidadPisos;
    private Integer cantidadCuartos;
    private Integer cantidadInquilinos;
    private Boolean estado;
}