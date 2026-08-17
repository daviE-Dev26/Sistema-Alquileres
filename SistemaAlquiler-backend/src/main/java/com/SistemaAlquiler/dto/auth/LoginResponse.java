package com.SistemaAlquiler.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

	private boolean success;
	private int code;
	private String message;
	
	private Integer codusu;
	private Integer codrol;
	private String nombre;	
}
