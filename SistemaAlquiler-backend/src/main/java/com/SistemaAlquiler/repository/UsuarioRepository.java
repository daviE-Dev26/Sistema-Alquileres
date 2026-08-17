package com.SistemaAlquiler.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SistemaAlquiler.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{

	Optional<Usuario> findByCorusu(String corusu);
	List<Usuario> findByEstusu(String estusu);
	Optional<Usuario> findByDocusu(String docusu);
	List<Usuario> findByEstusuBefore(
		    String estado,
		    LocalDate fecha
		);
	List<Usuario> findByEstusuAndFecusuBefore(
	        String estado,
	        LocalDate fecha
	);
}
