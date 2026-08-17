package com.SistemaAlquiler.dto.usuario;

import java.sql.Date;

import lombok.Data;

@Data
public class ActualizarPerfilAdminDTO {

	private String nomusu;
	private String apepusu;
	private String apemusu;
	private String docusu;
	private String dirusu;
	private Date fecusu;
	private String celusu;
	private String corusu;

	private String estusu;

	private Integer codrol;
	private Integer codtipdoc;
	
}
