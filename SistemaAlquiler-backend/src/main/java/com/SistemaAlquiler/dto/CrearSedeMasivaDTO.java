package com.SistemaAlquiler.dto;

import java.util.List;

import lombok.Data;
@Data
public class CrearSedeMasivaDTO {

    private String nombre;
    private String direccion;
    private String descripcion;

    private Integer codusu;

    private Integer cantidadPisos;

    private List<Integer> cuartosPorPiso;
    private Double precioCuarto;

}
