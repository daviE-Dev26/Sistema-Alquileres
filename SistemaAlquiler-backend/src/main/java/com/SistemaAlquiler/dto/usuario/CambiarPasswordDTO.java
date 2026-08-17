package com.SistemaAlquiler.dto.usuario;

import lombok.Data;

@Data
public class CambiarPasswordDTO {
	
	private String oldPassword;
	private String newPassword;

}
