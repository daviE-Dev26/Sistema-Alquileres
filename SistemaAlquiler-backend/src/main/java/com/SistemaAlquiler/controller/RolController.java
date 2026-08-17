package com.SistemaAlquiler.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.SistemaAlquiler.dto.RolDTO;
import com.SistemaAlquiler.entity.Rol;
import com.SistemaAlquiler.service.RolService;



@RestController 	
@RequestMapping("/rol")
public class RolController {

	@Autowired
	private RolService rolService;
	
	@GetMapping
	public List<Rol> getAll() {
	    return rolService.findAll();
	}	
	
	@GetMapping("/{id}")
	public Rol getById(@PathVariable int id) {
		return rolService.findById(id);
	}
	
	@PostMapping
	public Rol create(@RequestBody RolDTO rolDto) {
		return rolService.create(rolDto);
	}
	
	@PutMapping("/{id}")
	public Rol update(@PathVariable int id,@RequestBody RolDTO rolDto) {
		return rolService.update(id,rolDto);
	}
		
	@PutMapping("{id}/estado")
	public void cambiarEstado(@PathVariable int id,@RequestParam boolean estado) {
		rolService.cambiarEstado(id,estado);
	}	
}
