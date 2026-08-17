package com.SistemaAlquiler.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetricasDashboardDTO {

    private Double ingresos;
    private Double gastos;
    private Double ganancia;
    private Integer sedes;
    private Integer cuartos;
    private Integer inquilinos;
    private Integer disponibles;
    private Integer ocupados;
    private Double porcentajeOcupacion;
    private Double rentabilidad;
    private Double promedioPorCuarto;
    private Double promedioPorInquilino;
    private Double porcentajeGastos;
    private Double ingresoPotencial;
    private Double aumentoPotencial;
}