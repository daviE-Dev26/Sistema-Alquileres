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

import com.SistemaAlquiler.dto.TipoDocumentoDTO;
import com.SistemaAlquiler.entity.TipoDocumento;
import com.SistemaAlquiler.service.TipoDocumentoService;


@RestController
@RequestMapping("/tipodocumento")
public class TipoDocumentoController {

	@Autowired
	private TipoDocumentoService tipoDocumentoService;
	
	@GetMapping
	public List<TipoDocumento> getAll() {
	    return tipoDocumentoService.findAll();
	}	
	
	@GetMapping("/{id}")
	public TipoDocumento getById(@PathVariable int id) {
		return tipoDocumentoService.findById(id);
	}
	
	@PostMapping
	public TipoDocumento create(@RequestBody TipoDocumentoDTO dto) {
		return tipoDocumentoService.create(dto);
	}
	
	@PutMapping("/{id}")
	public TipoDocumento update(@PathVariable int id,@RequestBody TipoDocumentoDTO dto) {
		return tipoDocumentoService.update(id,dto);
	}
		
	@PutMapping("{id}/estado")
	public void cambiarEstado(@PathVariable int id,@RequestParam boolean estado) {
		tipoDocumentoService.cambiarEstado(id,estado);
	}	
	
}
