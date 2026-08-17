package com.SistemaAlquiler.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.SistemaAlquiler.service.UsuarioService;

@Configuration
public class DataInitializer {

	@Bean
    CommandLineRunner init(UsuarioService service) {
        return args -> {
            service.crearUsuariosIniciales();
        };
    }
	
}
