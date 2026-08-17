package com.SistemaAlquiler.dto;

import lombok.Data;

@Data
public class ServicioDTO {
	
	private String tipserv;
	private Double monto;
	private String coment;
	
	private Integer codusu;
	private Integer codsede;

}