package com.SistemaAlquiler.dto.auth;

import lombok.Data;

@Data
public class LoginRequest {

	private String correo;
	private String password;
	private int codrol;	
}
