package com.SistemaAlquiler.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.SistemaAlquiler.entity.PagoAlquiler;

@Repository
public interface PagoAlquilerRepository extends JpaRepository<PagoAlquiler,Integer>{

List<PagoAlquiler> findByUsuarioCodusu(Integer codusu);

List<PagoAlquiler> findByContratoCodasig(Integer codasig);

List<PagoAlquiler> findByContratoCodasigOrderByFechaRegistroDesc(
Integer codasig);
}