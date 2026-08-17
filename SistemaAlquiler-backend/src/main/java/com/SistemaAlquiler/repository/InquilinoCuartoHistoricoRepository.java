package com.SistemaAlquiler.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SistemaAlquiler.entity.InquilinoCuartoHistorico;

@Repository
public interface InquilinoCuartoHistoricoRepository
        extends JpaRepository<InquilinoCuartoHistorico, Integer> {

    List<InquilinoCuartoHistorico>
            findByInquilinoCodinqOrderByFechaMovimientoDesc(
                    Integer codinq
            );

    List<InquilinoCuartoHistorico>
            findByCodasigOrderByFechaMovimientoDesc(
                    Integer codasig
            );
    //nuevo
    List<InquilinoCuartoHistorico>
    findByInquilinoDocinqAndUsuarioCodusuOrderByFechaMovimientoDesc(
            String docinq,
            Integer codusu
    );
    
    List<InquilinoCuartoHistorico> findByUsuarioCodusu(Integer codusu);
}