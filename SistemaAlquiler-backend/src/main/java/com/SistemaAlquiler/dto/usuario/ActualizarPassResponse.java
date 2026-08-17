package com.SistemaAlquiler.dto.usuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActualizarPassResponse {

	private boolean success;
	private int codigo;
	private String mensaje;
	
}
