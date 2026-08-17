package com.SistemaAlquiler.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SistemaAlquiler.dto.CrearSedeDTO;
import com.SistemaAlquiler.dto.CrearSedeMasivaDTO;
import com.SistemaAlquiler.dto.ResumenSedeDTO;
import com.SistemaAlquiler.entity.Cuarto;
import com.SistemaAlquiler.entity.Piso;
import com.SistemaAlquiler.entity.Sede;
import com.SistemaAlquiler.entity.Usuario;
import com.SistemaAlquiler.repository.CuartoRepository;
import com.SistemaAlquiler.repository.InquilinoCuartoRepository;
import com.SistemaAlquiler.repository.PisoRepository;
import com.SistemaAlquiler.repository.SedeRepository;
import com.SistemaAlquiler.repository.UsuarioRepository;

@Service
public class SedeService {

    @Autowired
    private SedeRepository sedeRepository;
    @Autowired
    private CuartoRepository cuartoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PisoRepository pisoRepository;
    @Autowired
    private InquilinoCuartoRepository inquilinoCuartoRepository;
    public List<Sede> listarPorUsuario(Integer codusu){

        return sedeRepository.findByUsuarioCodusu(codusu);

    }

    public Sede crear(CrearSedeDTO dto){

        Usuario usuario =
                usuarioRepository.findById(dto.getCodusu())
                .orElse(null);

        if(usuario == null){
            return null;
        }

        Sede sede = new Sede();

        sede.setNombre(dto.getNombre());
        sede.setDireccion(dto.getDireccion());
        sede.setDescripcion(dto.getDescripcion());
        sede.setEstado(true);
        sede.setUsuario(usuario);

        return sedeRepository.save(sede);
    }

    public Sede editar(Integer id, CrearSedeDTO dto){

        Sede sede =
                sedeRepository.findById(id)
                .orElse(null);

        if(sede == null){
            return null;
        }

        sede.setNombre(dto.getNombre());
        sede.setDireccion(dto.getDireccion());
        sede.setDescripcion(dto.getDescripcion());

        return sedeRepository.save(sede);
    }

    public Sede habilitar(Integer id){

        Sede sede =
                sedeRepository.findById(id)
                .orElse(null);

        if(sede == null){
            return null;
        }

        sede.setEstado(true);

        return sedeRepository.save(sede);
    }

    public Sede deshabilitar(Integer id){

        Sede sede =
                sedeRepository.findById(id)
                .orElse(null);

        if(sede == null){
            return null;
        }

        sede.setEstado(false);

        return sedeRepository.save(sede);
    }
    public Sede crearMasiva(CrearSedeMasivaDTO dto){

        Usuario usuario =
                usuarioRepository.findById(dto.getCodusu())
                .orElse(null);

        if(usuario == null){
            return null;
        }

        // Crear sede
        Sede sede = new Sede();

        sede.setNombre(dto.getNombre());
        sede.setDireccion(dto.getDireccion());
        sede.setDescripcion(dto.getDescripcion());
        sede.setEstado(true);
        sede.setUsuario(usuario);

        sede = sedeRepository.save(sede);

        // Crear cuartos automáticamente
        for(int numeroPiso = 1;
        	    numeroPiso <= dto.getCantidadPisos();
        	    numeroPiso++) {

        	    Piso piso = new Piso();

        	    piso.setNumero(numeroPiso);
        	    piso.setEstado(true);
        	    piso.setSede(sede);

        	    piso = pisoRepository.save(piso);

        	    Integer cantidadCuartos =
        	        dto.getCuartosPorPiso().get(numeroPiso - 1);

        	    for(int numeroCuarto = 1;
        	        numeroCuarto <= cantidadCuartos;
        	        numeroCuarto++) {

        	        Cuarto cuarto = new Cuarto();

        	        int codigoCuarto =
        	            (numeroPiso * 100) + numeroCuarto;

        	        cuarto.setNumcuar(codigoCuarto);

        	        // ESTA LÍNEA ES LA IMPORTANTE
        	        cuarto.setPiso(piso);

        	        cuarto.setPasscuar(generarCodigoCuarto());
        	        cuarto.setDircuar(dto.getDireccion());
        	        //cuarto.setPreccuar(0.0);
        	        cuarto.setPreccuar(dto.getPrecioCuarto());
        	        cuarto.setFeccuar(LocalDate.now());
        	        cuarto.setDescuar("Sin descripción");
        	        cuarto.setFotocuar("default.jpg");
        	        cuarto.setEstcuar("Disponible");
        	        cuarto.setHabilitado(true);
        	        cuarto.setUsuario(usuario);
        	        cuarto.setSede(sede);

        	        cuartoRepository.save(cuarto);
        	    }
        	}

        return sede;
    }

    public List<ResumenSedeDTO> listarResumenPorUsuario(
            Integer codusu){

        List<Sede> sedes =
                sedeRepository
                .findByUsuarioCodusuAndEstado(
                        codusu,
                        true
                );

        List<ResumenSedeDTO> resultado =
                new ArrayList<>();

        for(Sede sede : sedes){

            ResumenSedeDTO dto =
                    new ResumenSedeDTO();

            dto.setCodsede(
                    sede.getCodsede()
            );

            dto.setNombre(
                    sede.getNombre()
            );

            dto.setDireccion(
                    sede.getDireccion()
            );

            dto.setDescripcion(
                    sede.getDescripcion()
            );

            dto.setCantidadPisos(
                    sede.getPisos().size()
            );

            int totalCuartos = 0;

            for(Piso piso : sede.getPisos()){

                totalCuartos +=
                        piso.getCuartos().size();
            }

            dto.setCantidadCuartos(
                    totalCuartos
            );
            int totalInquilinos = 0;

            for(Piso piso : sede.getPisos()){

                for(Cuarto cuarto : piso.getCuartos()){

                    totalInquilinos +=
                            inquilinoCuartoRepository
                            .findByCuartoCodcuar(
                                    cuarto.getCodcuar()
                            )
                            .size();
                }
            }

            dto.setCantidadInquilinos(
                    totalInquilinos
            );
            resultado.add(dto);
        }

        return resultado;
    }
    private String generarCodigoCuarto() {

        String caracteres =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        StringBuilder codigo =
                new StringBuilder();

        Random random = new Random();

        for(int i=0;i<6;i++){

            codigo.append(
                    caracteres.charAt(
                            random.nextInt(
                                    caracteres.length()
                            )
                    )
            );
        }

        return codigo.toString();
    }
}
