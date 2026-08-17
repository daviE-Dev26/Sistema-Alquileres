package com.SistemaAlquiler.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SistemaAlquiler.dto.ConfirmarPagoDTO;
import com.SistemaAlquiler.dto.DetallePeriodoDTO;
import com.SistemaAlquiler.dto.EstadoCuentaDTO;
import com.SistemaAlquiler.dto.PagoHistorialDTO;
import com.SistemaAlquiler.dto.culqi.CargoResponse;
import com.SistemaAlquiler.entity.DetallePagoAlquiler;
import com.SistemaAlquiler.entity.EstadoPago;
import com.SistemaAlquiler.entity.InquilinoCuarto;
import com.SistemaAlquiler.entity.MetodoPago;
import com.SistemaAlquiler.entity.PagoAlquiler;
import com.SistemaAlquiler.repository.DetallePagoAlquilerRepository;
import com.SistemaAlquiler.repository.InquilinoCuartoRepository;
import com.SistemaAlquiler.repository.PagoAlquilerRepository;
import org.springframework.jdbc.core.JdbcTemplate;

import jakarta.transaction.Transactional;

@Service
public class PagoAlquilerService{
	@Autowired
	private PagoAlquilerRepository pagoRepository;
	@Autowired
	private DetallePagoAlquilerRepository detalleRepository;
	@Autowired
	private InquilinoCuartoRepository contratoRepository;
	@Autowired
	private CulqiService culqiService;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Transactional
	private Double calcularMonto(
			InquilinoCuarto contrato,
			Integer cantidadMeses){

			return contrato.getMontoTotal()
			* cantidadMeses;
			}
	
	public List<PagoHistorialDTO> historialPago(
			Integer codasig){

			return pagoRepository
			.findByContratoCodasigOrderByFechaRegistroDesc(codasig)
			.stream()
			.map(this::convertirDTO)
			.toList();

			}
	private PagoHistorialDTO convertirDTO(
			PagoAlquiler pago){
			PagoHistorialDTO dto=
			new PagoHistorialDTO();


			dto.setCodpago(
			pago.getCodpago());


			dto.setMonto(
			pago.getMonto());


			dto.setCantidadMeses(
			pago.getCantidadMeses());


			dto.setPeriodoInicio(
			pago.getPeriodoInicio());


			dto.setPeriodoFin(
			pago.getPeriodoFin());


			dto.setMetodoPago(
			pago.getMetodoPago().name());


			dto.setEstadoPago(
			pago.getEstadoPago().name());


			dto.setFechaPago(
			pago.getFechaPago());


			dto.setObservacion(
			pago.getObservacion());


			// INQUILINO

			dto.setInquilino(
			pago.getContrato()
			.getInquilino()
			.getNominq()
			+ " "
			+
			pago.getContrato()
			.getInquilino()
			.getApepinq()
			+
			" "
			+
			pago.getContrato()
			.getInquilino()
			.getApeminq()
			);

			// CUARTO

			dto.setCuarto(
			String.valueOf(			
			pago.getContrato()
			.getCuarto()
			.getNumcuar())
			);


			return dto;

			}

	public EstadoCuentaDTO obtenerEstadoCuenta(
			Integer codasig){

			InquilinoCuarto contrato=
			contratoRepository.findById(codasig)
			.orElseThrow();

			List<DetallePagoAlquiler> detalles=
			detalleRepository
			.findByContratoCodasigOrderByAnioAscMesAsc(
			codasig);
			
			EstadoCuentaDTO dto=
			new EstadoCuentaDTO();
			dto.setInquilino(
			contrato.getInquilino()
			.getNominq()
			+
			" "
			+
			contrato.getInquilino()
			.getApepinq()
			+
			" "
			+
			contrato.getInquilino()
			.getApeminq()
			);
			dto.setCuarto(
			String.valueOf(
			contrato.getCuarto()
			.getNumcuar()
			)
			);
			Double montoMensual=
			contrato.getMontoTotal();
			dto.setMontoMensual(
			montoMensual
			);
			List<DetallePeriodoDTO> periodos=
			new ArrayList<>();
			for(DetallePagoAlquiler detalle: detalles){
			DetallePeriodoDTO periodo=
			new DetallePeriodoDTO();
			periodo.setAnio(
			detalle.getAnio()
			);
			periodo.setMes(
			detalle.getMes()
			);
			periodo.setMonto(
			detalle.getMonto()
			);
			periodo.setEstado(
			detalle.getEstado()
			);
			periodos.add(periodo);
			}
			dto.setPeriodos(
			periodos
			);
			if(!detalles.isEmpty()){
			DetallePagoAlquiler ultimo=
			detalles.get(
			detalles.size()-1
			);
			LocalDate ultimoPago=
			LocalDate.of(
			ultimo.getAnio(),
			ultimo.getMes(),
			1
			);
			dto.setUltimoPago(
			ultimoPago
			);
			dto.setProximoPago(
			ultimoPago.plusMonths(1)
			);
			}else{
			dto.setUltimoPago(null);
			dto.setProximoPago(
			LocalDate.now()
			);
			}
			long pagados=
					detalles.stream()
					.filter(d->d.getEstado().equals("PAGADO"))
					.count();

					dto.setMesesPagados(
					(int)pagados
					);
			long pendientes=
					detalles.stream()
					.filter(d->d.getEstado().equals("PENDIENTE"))
					.count();

					dto.setMesesPendientes(
					(int)pendientes
					);
			dto.setDeudaActual(
			0.0
			);
			return dto;
			}
	@Transactional
	public void crearDeudaInicial(InquilinoCuarto contrato){

	for(int i=0;i<24;i++){

		DetallePagoAlquiler detalle=
				new DetallePagoAlquiler();

				detalle.setContrato(
				contrato
				);

				LocalDate fecha=
				contrato.getFechin()
				.plusMonths(i);

		detalle.setAnio(
		fecha.getYear()
		);

		detalle.setMes(
		fecha.getMonthValue()
		);

		detalle.setMonto(
		contrato.getMontoTotal()
		);

		detalle.setEstado(
		"PENDIENTE"
		);
		detalle.setFechaPago(null);
		detalleRepository.save(detalle);

		}
	}
	@Transactional
	public PagoAlquiler confirmarPago(
			ConfirmarPagoDTO dto){	
	List<DetallePagoAlquiler> pendientes=
			detalleRepository
			.findByContratoCodasigAndEstadoOrderByAnioAscMesAsc(
					dto.getCodasig(),
			"PENDIENTE"
			);
	if(pendientes.isEmpty()){
		throw new RuntimeException(
		"El contrato no tiene meses pendientes."
		);
		}
	if(
			dto.getCantidadMeses()>
			pendientes.size()
			){
			throw new RuntimeException(
			"No existen suficientes meses pendientes."
			);
			}
	List<DetallePagoAlquiler> mesesAPagar=
			pendientes.subList(
			0,
			dto.getCantidadMeses()
			);
			String correo =
			mesesAPagar.get(0)
			.getContrato()
			.getInquilino()
			.getCorinq();
			Double montoTotal =
			mesesAPagar.stream()
			.mapToDouble(
			DetallePagoAlquiler::getMonto
			)
			.sum();
			PagoAlquiler pago =
					new PagoAlquiler();
			pago.setMetodoPago(
			        dto.getMetodoPago()
			);
			if(dto.getMetodoPago() == MetodoPago.CULQI){

			    CargoResponse cargo =
			    culqiService.crearCargo(
			        dto,
			        montoTotal,
			        correo
			    );

			    pago.setCulqiChargeId(
			        cargo.getId()
			    );

			    pago.setNumeroOperacion(
			        cargo.getAuthorization_code()
			    );

			}else{

			    pago.setNumeroOperacion(
			        dto.getNumeroOperacion()
			    );

			}
			pago.setContrato(
					mesesAPagar.get(0).getContrato()
					);

					pago.setMonto(
					montoTotal
					);
			pago.setUsuario(
			mesesAPagar.get(0)
			.getContrato()
			.getUsuario()
			);

			pago.setCantidadMeses(
			dto.getCantidadMeses()
			);

			pago.setObservacion(
			dto.getObservacion()
			);

			if(dto.getMetodoPago() == MetodoPago.CULQI){

			    pago.setOrigenPago("ONLINE");

			}else{

			    pago.setOrigenPago("MANUAL");

			}

			if(dto.getMetodoPago() == MetodoPago.CULQI){

			    // después de confirmar respuesta Culqi
			    pago.setEstadoPago(
			        EstadoPago.PAGADO
			    );

			}else{

			    pago.setEstadoPago(
			        EstadoPago.PAGADO
			    );

			}

			pago.setFechaPago(
			LocalDateTime.now()
			);

			pago.setFechaRegistro(
			LocalDateTime.now()
			);
					
			DetallePagoAlquiler primero=
			mesesAPagar.get(0);

			DetallePagoAlquiler ultimo=
			mesesAPagar.get(
			mesesAPagar.size()-1
			);

			pago.setPeriodoInicio(
			LocalDate.of(
			primero.getAnio(),
			primero.getMes(),
			1
			)
			);
			pago.setPeriodoFin(
			LocalDate.of(
			ultimo.getAnio(),
			ultimo.getMes(),
			1
			)
			);
			pago.setNumeroComprobante(
					generarNumeroComprobante()
					);
			PagoAlquiler pagoGuardado=
			pagoRepository.save(
			pago
			);
			
			for(DetallePagoAlquiler detalle : mesesAPagar){

				detalle.setPago(
				pagoGuardado
				);

				detalle.setEstado(
				"PAGADO"
				);

				detalle.setFechaPago(
				LocalDateTime.now()
				);

				detalleRepository.save(
				detalle
				);

				}
			
			
			// ✅ ACTUALIZAR FECHOUT DESPUÉS DEL PAGO EXITOSO
			contratoRepository.extenderFechaPorMeses(
			    pagoGuardado.getContrato().getCodasig(),
			    pagoGuardado.getCantidadMeses()
			);
			
			return pagoGuardado;
			
	}
	/*
	 * 
	Ya tienes implementado:

	✅ Un contrato genera automáticamente 24 meses pendientes.
	✅ Un pago puede cancelar 1, 2, 6 o 12 meses.
	✅ Cada mes sabe exactamente qué pago lo canceló.
	✅ Cada pago sabe cuántos meses pagó.
	✅ Ya tienes historial real.
	✅ Ya tienes trazabilidad para Culqi.

	Esto ya es una arquitectura bastante sólida.
	 **/
	private String generarNumeroComprobante(){

		Long siguiente=
		pagoRepository.count()+1;

		return String.format(
		"PAG-%08d",
		siguiente
		);

		}
	
	/**
	 * Obtiene los próximos 3 pagos pendientes del inquilino
	 */
	/**
	 * Obtiene los 3 próximos pagos pendientes + los últimos 2 pagos realizados
	 */
	public List<Map<String, Object>> obtenerPagosPorInquilino(Integer codinq) {
	    String sql = """
	        SELECT * FROM (
	            -- PARTE 1: Los 3 próximos meses cronológicos (Prioridad 1)
	            SELECT TOP 3
	                dp.anio,
	                dp.mes,
	                dp.monto,
	                dp.estado,
	                dp.fecha_pago,
	                pa.metodo_pago,
	                pa.numero_comprobante,
	                1 as orden_prioridad
	            FROM detalle_pago_alquiler dp
	            LEFT JOIN pago_alquiler pa ON dp.codpago = pa.codpago
	            INNER JOIN inquilino_cuarto ic ON dp.codasig = ic.codasig
	            WHERE ic.codinq = ? 
	            ORDER BY dp.anio ASC, dp.mes ASC
	            
	            UNION ALL
	            
	            -- PARTE 2: Últimos 2 pagos PAGADOS que NO estén ya en los próximos 3 meses
	            SELECT TOP 2
	                dp2.anio,
	                dp2.mes,
	                dp2.monto,
	                dp2.estado,
	                dp2.fecha_pago,
	                pa2.metodo_pago,
	                pa2.numero_comprobante,
	                2 as orden_prioridad
	            FROM detalle_pago_alquiler dp2
	            INNER JOIN pago_alquiler pa2 ON dp2.codpago = pa2.codpago
	            INNER JOIN inquilino_cuarto ic2 ON dp2.codasig = ic2.codasig
	            WHERE ic2.codinq = ? 
	              AND dp2.estado = 'PAGADO'
	              -- ✅ FILTRO CLAVE: Excluir si ya existe en la Parte 1
	              AND NOT EXISTS (
	                  SELECT 1 
	                  FROM detalle_pago_alquiler dp1
	                  INNER JOIN inquilino_cuarto ic1 ON dp1.codasig = ic1.codasig
	                  WHERE ic1.codinq = ?
	                    AND dp1.anio = dp2.anio 
	                    AND dp1.mes = dp2.mes
	              )
	            ORDER BY dp2.anio DESC, dp2.mes DESC
	        ) AS resultados_finales
	        ORDER BY orden_prioridad ASC, anio ASC, mes ASC
	    """;
	    
	    // Pasamos el codinq 3 veces: para Parte 1, Parte 2 y el NOT EXISTS
	    return jdbcTemplate.queryForList(sql, codinq, codinq, codinq);
	}
}

/*
✅ Registro de la deuda inicial al crear un contrato.
✅ Confirmación de pagos manuales.
✅ Pago de varios meses por adelantado.
✅ Historial de pagos.
✅ Estado de cuenta.
✅ Número de comprobante (PAG-00000001).
✅ Preparación para Culqi (estado del pago con enum).
 * */

