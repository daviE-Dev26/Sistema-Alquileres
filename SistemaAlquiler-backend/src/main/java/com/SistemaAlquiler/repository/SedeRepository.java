package com.SistemaAlquiler.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SistemaAlquiler.entity.Sede;

public interface SedeRepository
extends JpaRepository<Sede, Integer>{

	List<Sede> findByUsuarioCodusuAndEstado(
	        Integer codusu,
	        Boolean estado
	);
	List<Sede> findByUsuarioCodusu(Integer codusu);
	//NUEVO CAMBIO 30/06/26 12:06
	Integer countByUsuarioCodusu(Integer codusu);
}