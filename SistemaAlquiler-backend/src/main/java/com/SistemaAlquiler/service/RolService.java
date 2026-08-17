package com.SistemaAlquiler.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.SistemaAlquiler.dto.RolDTO;
import com.SistemaAlquiler.entity.Rol;
import com.SistemaAlquiler.repository.RolRepository;

@Service
public class RolService {
	
	@Autowired
	private RolRepository rolRepository;
	
	public List<Rol> findAll(){
		return rolRepository.findAll();
	}	

	public Rol findById(int id) {
        return rolRepository.findById(id).orElse(null);
    }
	
	public Rol create(RolDTO dto) {

        Rol rol = Rol.builder()
                .nomrol(dto.getNomrol())
                .estrol(dto.getEstrol())
                .build();

        return rolRepository.save(rol);
    }
	
	public Rol update(int id, RolDTO dto) {

        Rol rol = rolRepository.findById(id).orElse(null);

        if (rol == null) return null;

        rol.setNomrol(dto.getNomrol());
        rol.setEstrol(dto.getEstrol());

        return rolRepository.save(rol);
    }
	
	public void cambiarEstado(int id, boolean estado) {
		Rol rol = rolRepository.findById(id).orElse(null);

        if (rol == null) return;

        rol.setEstrol(estado);

        rolRepository.save(rol);
    }
	

}

