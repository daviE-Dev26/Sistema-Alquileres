package com.SistemaAlquiler.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // ✅ Importación añadida para usar consultas personalizadas
import org.springframework.data.repository.query.Param; // ✅ Importación añadida para mapear los parámetros (:docinq, :codusu)
import org.springframework.stereotype.Repository;
import com.SistemaAlquiler.entity.Inquilino;

@Repository
public interface InquilinoRepository extends JpaRepository<Inquilino, Integer> {
    
    // ✅ Busca todos los inquilinos asociados al código del propietario
    List<Inquilino> findByUsuarioCodusu(int codusu);

    // ✅ MODIFICADO: Forzamos la consulta exacta con @Query para aislar por completo el DNI por cada Propietario
    @Query("SELECT i FROM Inquilino i WHERE i.docinq = :docinq AND i.usuario.codusu = :codusu")
    Optional<Inquilino> findByDocinqAndUsuarioCodusu(
        @Param("docinq") String docinq, 
        @Param("codusu") int codusu
    );
    Optional<Inquilino> findByCodinq(Integer codinq);
  //NUEVO CAMBIO 30/06/26 12:06
    Integer countByUsuarioCodusu(Integer codusu);
}