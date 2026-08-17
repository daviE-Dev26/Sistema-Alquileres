package com.SistemaAlquiler.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SistemaAlquiler.dto.MetricasDashboardDTO;
import com.SistemaAlquiler.service.MetricasService;

@RestController
@RequestMapping("/metricas")
@CrossOrigin("*")
public class MetricasController {

    @Autowired
    private MetricasService metricasService;

    @GetMapping("/dashboard/{codusu}")
    public MetricasDashboardDTO dashboard(
            @PathVariable Integer codusu){

        return metricasService.obtenerDashboard(codusu);

    }
    @GetMapping("/dashboard/{codusu}/sede/{codsede}")
    public MetricasDashboardDTO dashboardPorSede(
            @PathVariable Integer codusu,
            @PathVariable Integer codsede){

        return metricasService.obtenerDashboardPorSede(
                codusu,
                codsede
        );

    }
    /*
    @GetMapping("/grafico/ingresos/{codusu}")
    public List<GraficoIngresoDTO> graficoIngresos(

            @PathVariable Integer codusu){

        return metricasService
                .obtenerGraficoIngresos(codusu);

    }*/
}