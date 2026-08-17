package com.SistemaAlquiler.dto;

import lombok.Data;
@Data
public class EditarCuartoDTO {
	
    private Integer codcuar;

    private Integer codsede;
    private String nombreSede;

    private Integer codpiso;
    private Integer numeroPiso;

    private Integer numcuar;

    private String dircuar;

    private Double preccuar;

    private String passcuar;

    private String descuar;
    private Boolean habilitado;
    private String estcuar;
}
