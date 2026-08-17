package com.SistemaAlquiler.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.SistemaAlquiler.entity.Cuarto;

@Repository
public interface CuartoRepository extends JpaRepository<Cuarto, Integer> {
	
	// ✅ NUEVO: Busca un cuarto por su código de acceso (passcuar) para el Login del Inquilino
	Optional<Cuarto> findByPasscuar(String passcuar);
	
	// Método original (Global)
	List<Cuarto> findByEstcuar(String estado);

	// ✅ NUEVO: Busca todos los cuartos que le pertenecen a un propietario específico
	List<Cuarto> findByUsuarioCodusu(int codusu);
	
	// ✅ NUEVO: Busca los cuartos de un estado específico (ej. "Disponible") que le pertenezcan a ese propietario
	List<Cuarto> findByEstcuarAndUsuarioCodusu(String estado, int codusu);

	// ✅ MODIFICADO: Se añade @Query para asegurar que Hibernate filtre correctamente por tipos Integer y aísle por Propietario
	//@Query("SELECT c FROM Cuarto c WHERE c.numcuar = :numcuar AND c.pisocuar = :pisocuar AND c.usuario.codusu = :codusu")
	/*Optional<Cuarto> findByNumcuarAndPisocuarAndUsuarioCodusu(
		@Param("numcuar") Integer numcuar, 
		@Param("pisocuar") Integer pisocuar, 
		@Param("codusu") int codusu
	);*/
	List<Cuarto> findBySedeCodsede(Integer codsede);
	List<Cuarto> findByPisoCodpiso(Integer codpiso);

	Optional<Cuarto> findByNumcuarAndPisoCodpisoAndUsuarioCodusu(Integer numcuar, Integer numeroPiso, Integer codusu);

	List<Cuarto> findByPisoCodpisoAndEstcuar(Integer codpiso, String estado);

	boolean existsByPasscuar(String passcuar);

	List<Cuarto> findByPisoCodpisoOrderByNumcuarAsc(Integer codpiso);
	
	List<Cuarto> findByPisoCodpisoAndEstcuarAndHabilitado(
		    Integer codpiso,
		    String estado,
		    Boolean habilitado
		);
	List<Cuarto> findByEstcuarAndUsuarioCodusuAndHabilitado(
		    String estado,
		    Integer codusu,
		    Boolean habilitado
		);
	//NUEVO CAMBIO 30/06/26 12:06
	Integer countByUsuarioCodusu(Integer codusu);
	@Query("""
		    SELECT COALESCE(SUM(c.preccuar),0)
		    FROM Cuarto c
		    WHERE c.usuario.codusu = :codusu
		    AND c.codcuar NOT IN (
		        SELECT ic.cuarto.codcuar
		        FROM InquilinoCuarto ic
		        WHERE ic.estado = true
		    )
		""")
		Double obtenerIngresoPotencial(Integer codusu);
	Integer countBySedeCodsede(Integer codsede);
}