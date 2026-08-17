package com.SistemaAlquiler.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SistemaAlquiler.dto.ResumenPisoDTO;
import com.SistemaAlquiler.entity.Piso;
import com.SistemaAlquiler.service.PisoService;

@RestController
@RequestMapping("/piso")
public class PisoController {

    @Autowired
    private PisoService pisoService;

    @GetMapping("/sede/{codsede}")
    public List<Piso> listarPorSede(
            @PathVariable Integer codsede){

        return pisoService.listarPorSede(codsede);
    }
    @GetMapping("/resumen/sede/{codsede}")
    public List<ResumenPisoDTO> listarResumenPorSede(
            @PathVariable Integer codsede){

        return pisoService.listarResumenPorSede(codsede);

    }
    @GetMapping("/usuario/{codusu}")
    public List<ResumenPisoDTO> listarPorUsuario(
            @PathVariable Integer codusu){

        return pisoService.listarResumenPorUsuario(codusu);

    }    
}
