package com.SistemaAlquiler.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SistemaAlquiler.dto.MetricasDashboardDTO;
import com.SistemaAlquiler.repository.CuartoRepository;
import com.SistemaAlquiler.repository.InquilinoCuartoRepository;
import com.SistemaAlquiler.repository.InquilinoRepository;
import com.SistemaAlquiler.repository.SedeRepository;
import com.SistemaAlquiler.repository.ServicioRepository;

@Service
public class MetricasService {

    @Autowired
    private InquilinoCuartoRepository contratoRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private SedeRepository sedeRepository;

    @Autowired
    private CuartoRepository cuartoRepository;

    @Autowired
    private InquilinoRepository inquilinoRepository;

    public MetricasDashboardDTO obtenerDashboard(Integer codusu){

    	Double ingresos =
    	        contratoRepository.obtenerIngresosMensuales(codusu);

    	Double gastos =
    	        servicioRepository.obtenerTotalServicios(codusu);

    	System.out.println("INGRESOS = " + ingresos);
    	System.out.println("GASTOS = " + gastos);

        Double ganancia = ingresos - gastos;

        Integer sedes =
                sedeRepository.countByUsuarioCodusu(codusu);

        Integer cuartos =
                cuartoRepository.countByUsuarioCodusu(codusu);

        Integer inquilinos =
                inquilinoRepository.countByUsuarioCodusu(codusu);

        Integer ocupados =
                contratoRepository.countCuartosOcupados(codusu);

        Integer disponibles =
                cuartos - ocupados;
        Double rentabilidad = 0.0;
        Double porcentajeOcupacion = 0.0;
        Double promedioPorCuarto = 0.0;
        Double promedioPorInquilino = 0.0;
        Double porcentajeGastos = 0.0;
        Double aumentoPotencial =
                cuartoRepository.obtenerIngresoPotencial(codusu);

        Double ingresoPotencial =
                ingresos + aumentoPotencial;
        if (cuartos > 0) {

            porcentajeOcupacion =
                    (ocupados.doubleValue() / cuartos.doubleValue()) * 100;

            promedioPorCuarto =
                    ingresos / cuartos.doubleValue();

        }

        if (inquilinos > 0) {

            promedioPorInquilino =
                    ganancia / inquilinos.doubleValue();

        }

        if (ingresos > 0) {

            rentabilidad =
                    (ganancia / ingresos) * 100;

            porcentajeGastos =
                    (gastos / ingresos) * 100;

        }
        return new MetricasDashboardDTO(

        	    ingresos,
        	    gastos,
        	    ganancia,

        	    sedes,
        	    cuartos,
        	    inquilinos,

        	    disponibles,
        	    ocupados,

        	    porcentajeOcupacion,
        	    rentabilidad,

        	    promedioPorCuarto,
        	    promedioPorInquilino,
        	    porcentajeGastos,
        	    aumentoPotencial,
        	    ingresoPotencial

        	);

    }
    
    public MetricasDashboardDTO obtenerDashboardPorSede(
            Integer codusu,
            Integer codsede){


        Double ingresos =
                contratoRepository.obtenerIngresosMensualesPorSede(
                        codusu,
                        codsede);


        Double gastos =
                servicioRepository.obtenerTotalServiciosPorSede(
                        codusu,
                        codsede);


        Double ganancia = ingresos - gastos;


        Integer cuartos =
                cuartoRepository.countBySedeCodsede(codsede);


        Integer ocupados =
                contratoRepository.countCuartosOcupadosPorSede(
                        codsede);


        Integer disponibles =
                cuartos - ocupados;


        Integer inquilinos =
                contratoRepository.countInquilinosPorSede(codsede);



        Double porcentajeOcupacion =
                cuartos == 0
                ? 0
                : (ocupados * 100.0) / cuartos;



        Double rentabilidad =
                ingresos == 0
                ? 0
                : (ganancia * 100) / ingresos;



        Double porcentajeGastos =
                ingresos == 0
                ? 0
                : (gastos * 100) / ingresos;



        return new MetricasDashboardDTO(

                ingresos,
                gastos,
                ganancia,

                1,
                cuartos,
                inquilinos,

                disponibles,
                ocupados,

                porcentajeOcupacion,
                rentabilidad,

                cuartos == 0 ? 0 : ingresos / cuartos,
                inquilinos == 0 ? 0 : ganancia / inquilinos,

                porcentajeGastos,
                0.0,
                ingresos

        );

    }
    /*public List<GraficoIngresoDTO> obtenerGraficoIngresos(
            Integer codusu){

        return contratoRepository
                .obtenerGraficoIngresosMensuales(codusu);

    }*/
}