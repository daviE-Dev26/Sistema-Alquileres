package com.SistemaAlquiler.dto.auth;

import java.time.LocalDate;

import lombok.Data;

@Data
public class LoginInquilinoDTO {

	private Integer codinq;
    private String nominq;
    private String apepinq;
    private String apeminq;
    private String docinq;
    private String celinq;
    private String corinq;

    private Integer codcuar;
    private Integer numcuar;
    private String dircuar;
    private Double preccuar;
    private String estcuar;

    private Integer codsede;
    private String nomsede;

    private Integer codpiso;
    private Integer numpiso;

    private Integer codasig;
    private LocalDate fechin;
    private LocalDate fechout;
    private Double montoTotal;
    private Boolean estadoContrato;
	
}