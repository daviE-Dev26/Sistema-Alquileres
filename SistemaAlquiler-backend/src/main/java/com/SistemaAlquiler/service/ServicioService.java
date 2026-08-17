package com.SistemaAlquiler.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SistemaAlquiler.dto.ServicioDTO;
import com.SistemaAlquiler.entity.Sede;
import com.SistemaAlquiler.entity.Servicio;
import com.SistemaAlquiler.entity.Usuario;
import com.SistemaAlquiler.repository.SedeRepository;
import com.SistemaAlquiler.repository.ServicioRepository;
import com.SistemaAlquiler.repository.UsuarioRepository;

@Service
public class ServicioService {

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SedeRepository sedeRepository;

    public List<Servicio> listarServicios(Integer codusu) {
        return servicioRepository.findByUsuarioCodusu(codusu);
    }

    public List<Servicio> listarPorTipo(Integer codusu, String tipserv) {    	
        return servicioRepository.findByUsuarioCodusuAndTipserv(codusu, tipserv);
    }

    public Servicio crearServicio(ServicioDTO dto) {

        Usuario usuario = usuarioRepository.findById(dto.getCodusu())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Sede sede = sedeRepository.findById(dto.getCodsede())
                .orElseThrow(() -> new RuntimeException("Sede no encontrada"));

        Servicio servicio = new Servicio();

        servicio.setTipserv(dto.getTipserv());
        servicio.setMonto(dto.getMonto());
        servicio.setFeching(LocalDate.now());
        servicio.setComent(dto.getComent());
        servicio.setUsuario(usuario);
        servicio.setSede(sede);

        return servicioRepository.save(servicio);
    }

    public Servicio actualizarServicio(Integer codserv, ServicioDTO dto) {

        Servicio servicio = servicioRepository.findById(codserv)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        servicio.setTipserv(dto.getTipserv());
        servicio.setMonto(dto.getMonto());
        servicio.setComent(dto.getComent());

        if (dto.getCodsede() != null) {
            Sede sede = sedeRepository.findById(dto.getCodsede())
                    .orElseThrow(() -> new RuntimeException("Sede no encontrada"));

            servicio.setSede(sede);
        }

        return servicioRepository.save(servicio);
    }
    
    public List<Servicio> listarPorSede(Integer codusu, Integer codsede) {
        return servicioRepository.findByUsuarioCodusuAndSedeCodsede(codusu, codsede);
    }
    
}