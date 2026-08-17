package com.SistemaAlquiler.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.SistemaAlquiler.entity.DetallePagoAlquiler;

@Repository
public interface DetallePagoAlquilerRepository extends JpaRepository<DetallePagoAlquiler,Integer>{

List<DetallePagoAlquiler> findByPagoCodpago(Integer codpago);

@Query("""
SELECT COUNT(d)>0
FROM DetallePagoAlquiler d
WHERE d.contrato.codasig=:codasig
AND d.anio=:anio
AND d.mes=:mes
""")
boolean existePagoMes(
        Integer codasig,
        Integer anio,
        Integer mes);

List<DetallePagoAlquiler> findByContratoCodasigOrderByAnioAscMesAsc(
Integer codasig);

boolean existsByContratoCodasigAndAnioAndMes(
Integer codasig,
Integer anio,
Integer mes);
List<DetallePagoAlquiler>
findByContratoCodasigAndEstadoOrderByAnioAscMesAsc(
Integer codasig,
String estado
);
}
