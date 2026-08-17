package com.SistemaAlquiler.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.SistemaAlquiler.entity.Piso;

public interface PisoRepository
extends JpaRepository<Piso, Integer>{
    List<Piso> findBySedeCodsede(Integer codsede);
    List<Piso> findBySedeUsuarioCodusu(Integer codusu);    
}