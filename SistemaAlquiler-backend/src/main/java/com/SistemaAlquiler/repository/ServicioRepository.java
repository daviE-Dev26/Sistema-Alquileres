package com.SistemaAlquiler.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.SistemaAlquiler.entity.Servicio;

public interface ServicioRepository extends JpaRepository<Servicio, Integer> {

    List<Servicio> findByUsuarioCodusu(Integer codusu);

    List<Servicio> findByUsuarioCodusuAndTipserv(Integer codusu, String tipserv);
    
    List<Servicio> findByUsuarioCodusuAndSedeCodsede(Integer codusu, Integer codsede);
    //NUEVO CAMBIO 30/06/26 12:06
    @Query("""
    		SELECT COALESCE(SUM(s.monto),0)
    		FROM Servicio s
    		WHERE s.usuario.codusu = :codusu
    		""")
    		Double obtenerTotalServicios(Integer codusu);
    @Query("""
    		SELECT COALESCE(SUM(s.monto),0)
    		FROM Servicio s
    		WHERE s.usuario.codusu = :codusu
    		AND s.sede.codsede = :codsede
    		""")
    		Double obtenerTotalServiciosPorSede(
    		        Integer codusu,
    		        Integer codsede);
}