package com.SistemaAlquiler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.SistemaAlquiler.entity.NotificacionMorosidad;

public interface NotificacionMorosidadRepository extends JpaRepository<NotificacionMorosidad, Integer> {
    
    // ✅ Retorna boolean directamente - No falla con duplicados
    boolean existsByCodinqAndDiasMoraAndTipoNotificacion(
        Integer codinq, Integer diasMora, String tipoNotificacion
    );
}