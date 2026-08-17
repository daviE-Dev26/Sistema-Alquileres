package com.SistemaAlquiler.service;

import com.SistemaAlquiler.repository.InquilinoCuartoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class InquilinoCuartoService {

    private static final Logger logger = LoggerFactory.getLogger(InquilinoCuartoService.class);

    @Autowired
    private InquilinoCuartoRepository inquilinoCuartoRepository;

    @Transactional
    public void extenderMesConPago(Integer codasig, Integer codusu) {
        logger.info("Iniciando extensión de mes para codasig: {}", codasig);
        
        // 1. Obtener información del inquilino-cuarto
        Map<String, Object> info = inquilinoCuartoRepository.obtenerInfoParaPago(codasig);
        
        logger.info("Información obtenida: {}", info);
        
        if (info == null || info.isEmpty()) {
            logger.error("No se encontró la asignación con codasig: {}", codasig);
            throw new RuntimeException("No se encontró el contrato con código: " + codasig);
        }

        // Conversión segura
        Object montoObj = info.get("montoTotal");
        Double montoTotal = montoObj instanceof Number ? ((Number) montoObj).doubleValue() : 0.0;
        
        logger.info("Monto total: {}", montoTotal);
        
        // 2. Calcular período de pago
        LocalDate ahora = LocalDate.now();
        String periodoInicio = ahora.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String periodoFin = ahora.plusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        // 3. Insertar en pago_alquiler
        logger.info("Insertando pago_alquiler...");
        try {
            inquilinoCuartoRepository.insertarPagoAlquiler(
                codasig, 
                codusu, 
                montoTotal, 
                periodoInicio, 
                periodoFin
            );
            logger.info("Pago insertado correctamente");
        } catch (Exception e) {
            logger.error("Error al insertar pago: {}", e.getMessage());
            throw e;
        }
        
        // 4. Obtener el codpago generado
        logger.info("Obteniendo último codpago...");
        Integer codpago = inquilinoCuartoRepository.obtenerUltimoCodPago(codasig);
        
        if (codpago == null) {
            logger.error("No se pudo obtener el código del pago");
            throw new RuntimeException("No se pudo obtener el código del pago recién insertado");
        }
        
        logger.info("Marcando detalle pendiente como pagado...");
        inquilinoCuartoRepository.marcarDetalleComoPagado(
            codpago,
            codasig,
            ahora.getYear(),
            ahora.getMonthValue()
        );
        
        // 6. Extender la fecha de pago (fechout)
        logger.info("Extendiendo fecha de pago...");
        inquilinoCuartoRepository.extenderFechaPago(codasig);
        
        logger.info("Proceso completado exitosamente");
    }
}