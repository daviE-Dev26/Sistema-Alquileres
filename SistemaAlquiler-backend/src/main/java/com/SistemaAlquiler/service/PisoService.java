package com.SistemaAlquiler.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SistemaAlquiler.dto.ResumenPisoDTO;
import com.SistemaAlquiler.entity.Cuarto;
import com.SistemaAlquiler.entity.Piso;
import com.SistemaAlquiler.repository.InquilinoCuartoRepository;
import com.SistemaAlquiler.repository.PisoRepository;

@Service
public class PisoService {

    @Autowired
    private PisoRepository pisoRepository;
    @Autowired
    private InquilinoCuartoRepository inquilinoCuartoRepository;
    public List<Piso> listarPorSede(Integer codsede){

        return pisoRepository.findBySedeCodsede(codsede);

    }
    public List<ResumenPisoDTO> listarResumenPorSede(
            Integer codsede){

        List<Piso> pisos =
                pisoRepository.findBySedeCodsede(codsede);

        List<ResumenPisoDTO> resultado =
                new ArrayList<>();

        for(Piso piso : pisos){

            int totalInquilinos = 0;

            for(Cuarto cuarto : piso.getCuartos()){

                totalInquilinos +=
                        inquilinoCuartoRepository
                        .findByEstadoTrueAndCuartoCodcuar(
                                cuarto.getCodcuar())
                        .size();
            }

            resultado.add(

                new ResumenPisoDTO(

                        piso.getCodpiso(),
                        piso.getNumero(),
                        piso.getEstado(),
                        piso.getCuartos().size(),
                        totalInquilinos,
                        piso.getSede().getNombre(),
                        piso.getSede().getDireccion()

                )
            );
        }

        return resultado;
    }
    public List<ResumenPisoDTO> listarResumenPorUsuario(Integer codusu){

        List<Piso> pisos =
                pisoRepository.findBySedeUsuarioCodusu(codusu);

        List<ResumenPisoDTO> lista = new ArrayList<>();

        for(Piso piso : pisos){

            int totalCuartos = piso.getCuartos().size();

            int totalInquilinos = 0;

            for(Cuarto cuarto : piso.getCuartos()){

                totalInquilinos +=
                        inquilinoCuartoRepository
                        .findByEstadoTrueAndCuartoCodcuar(
                                cuarto.getCodcuar())
                        .size();

            }

            lista.add(

            	    new ResumenPisoDTO(

            	        piso.getCodpiso(),
            	        piso.getNumero(),
            	        piso.getEstado(),
            	        totalCuartos,
            	        totalInquilinos,
            	        piso.getSede().getNombre(),
            	        piso.getSede().getDireccion()

            	    )

            	);

        }

        return lista;
    }

}
