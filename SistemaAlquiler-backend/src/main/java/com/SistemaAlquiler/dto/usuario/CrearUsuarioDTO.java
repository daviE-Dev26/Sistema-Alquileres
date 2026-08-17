package com.SistemaAlquiler.dto.usuario;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CrearUsuarioDTO {
	
	private String nomusu;
	private String apepusu;
	private String apemusu;
	private String docusu;
	private String dirusu;
	private LocalDate  fecusu;
	private String celusu;
	private String corusu;

	private String password;

	private Integer codrol;
	private Integer codtipdoc;

}
