package com.SistemaAlquiler.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SistemaAlquiler.entity.InquilinoHistorico;

@Repository
public interface InquilinoHistoricoRepository
        extends JpaRepository<InquilinoHistorico, Integer> {

    List<InquilinoHistorico>
            findByCodinqOrderByFechaMovimientoDesc(Integer codinq);
    //nuevo
    List<InquilinoHistorico>
    findByDocinqAndUsuarioCodusuOrderByFechaMovimientoDesc(
            String docinq,
            Integer codusu
    );
    
    List<InquilinoHistorico> findByUsuarioCodusu(Integer codusu);
    
}