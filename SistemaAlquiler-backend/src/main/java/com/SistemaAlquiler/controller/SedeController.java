package com.SistemaAlquiler.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SistemaAlquiler.dto.CrearSedeDTO;
import com.SistemaAlquiler.dto.CrearSedeMasivaDTO;
import com.SistemaAlquiler.dto.ResumenSedeDTO;
import com.SistemaAlquiler.entity.Sede;
import com.SistemaAlquiler.service.SedeService;

@RestController
@RequestMapping("/sede")
public class SedeController {

    @Autowired
    private SedeService sedeService;

    @GetMapping("/usuario/{codusu}")
    public List<Sede> listarPorUsuario(
            @PathVariable("codusu") Integer codusu){

        return sedeService.listarPorUsuario(codusu);
    }
    @GetMapping("/resumen/usuario/{codusu}")
    public List<ResumenSedeDTO> listarResumenPorUsuario(
            @PathVariable Integer codusu){

        return sedeService.listarResumenPorUsuario(codusu);
    }
    @PostMapping
    public Sede crear(
            @RequestBody CrearSedeDTO dto){

        return sedeService.crear(dto);
    }

    @PutMapping("/{id}")
    public Sede editar(
            @PathVariable("id") Integer id,
            @RequestBody CrearSedeDTO dto){

        return sedeService.editar(id, dto);
    }

    @PutMapping("/habilitar/{id}")
    public Sede habilitar(
            @PathVariable("id") Integer id){

        return sedeService.habilitar(id);
    }

    @PutMapping("/deshabilitar/{id}")
    public Sede deshabilitar(
            @PathVariable("id") Integer id){

        return sedeService.deshabilitar(id);
    }
    @PostMapping("/masiva")
    public Sede crearMasiva(
            @RequestBody CrearSedeMasivaDTO dto){
        return sedeService.crearMasiva(dto);
    }
}
