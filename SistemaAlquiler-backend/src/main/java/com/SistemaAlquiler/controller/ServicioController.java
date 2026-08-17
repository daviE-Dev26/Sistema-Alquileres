package com.SistemaAlquiler.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.SistemaAlquiler.dto.ServicioDTO;
import com.SistemaAlquiler.entity.Servicio;
import com.SistemaAlquiler.service.ServicioService;

@RestController
@RequestMapping("/servicio")
@CrossOrigin("*")
public class ServicioController {

    @Autowired
    private ServicioService servicioService;

    @GetMapping("/usuario/{codusu}")
    public List<Servicio> listarServicios(@PathVariable Integer codusu) {
        return servicioService.listarServicios(codusu);
    }

    @GetMapping("/usuario/{codusu}/tipo/{tipo}")
    public List<Servicio> listarPorEstado(
            @PathVariable Integer codusu,
            @PathVariable String tipo) {

        return servicioService.listarPorTipo(codusu, tipo);
    }

    @PostMapping("/create")
    public Servicio crearServicio(@RequestBody ServicioDTO dto) {
        return servicioService.crearServicio(dto);
    }

    @PutMapping("/update/{codserv}")
    public Servicio actualizarServicio(
            @PathVariable Integer codserv,
            @RequestBody ServicioDTO dto) {

        return servicioService.actualizarServicio(codserv, dto);
    }
    
    @GetMapping("/usuario/{codusu}/sede/{codsede}")
    public List<Servicio> listarPorSede(
            @PathVariable Integer codusu,
            @PathVariable Integer codsede) {

        return servicioService.listarPorSede(codusu, codsede);
    }
    
}