package com.SistemaAlquiler.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CuartoDTO {

    private Integer codcuar;

    private Integer numcuar;
    private String passcuar;
    private String dircuar;
    private Double preccuar;
    private LocalDate feccuar;
    private String descuar;
    private String fotocuar;
    private String estcuar;
    private Boolean habilitado;
    private Integer codusu;
    private Integer codsede;

    private String nombreCompleto;
    private String nombreSede;
    private Integer numeroPiso;
}