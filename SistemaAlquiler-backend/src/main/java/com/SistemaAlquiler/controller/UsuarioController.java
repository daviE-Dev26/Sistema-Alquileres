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

import com.SistemaAlquiler.dto.auth.LoginRequest;
import com.SistemaAlquiler.dto.auth.LoginResponse;
import com.SistemaAlquiler.dto.usuario.ActualizarPerfilDTO;
import com.SistemaAlquiler.entity.Usuario;
import com.SistemaAlquiler.service.UsuarioService;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;
	
	@GetMapping
	public List<Usuario> getAll() {
		return usuarioService.findAll();
	}
	
	@GetMapping("/{id}")
	public Usuario getById(@PathVariable int id) {
		return usuarioService.findById(id);
	}
	/*
	@GetMapping("/pendientes")
	public List<Usuario> listarPendientes(){
	    return usuarioService.listarPendientes();
	}
	@PostMapping
	public Usuario create(@RequestBody CrearUsuarioDTO dto) {
		return usuarioService.create(dto);		
	}*/
	
	@PostMapping("/login")
	public LoginResponse login(@RequestBody LoginRequest req) {
		return usuarioService.login(req);
	}
	
	@PutMapping("/perfil/{id}")
	public Usuario actualizarPerfil(@PathVariable int id,
								 @RequestBody ActualizarPerfilDTO dto) {
		return usuarioService.ActualizarPerfil(id, dto);
	}
/*
	@PutMapping("/admin/{id}")
	public Usuario actualizarPerfilAdmin(@PathVariable int id,
									 @RequestBody ActualizarPerfilAdminDTO dto) {
		return usuarioService.ActualizarPerfilAdmin(id, dto);
	}

	@PutMapping("/password/{id}")
	public ActualizarPassResponse cambiarPassword(@PathVariable int id,
												 @RequestBody CambiarPasswordDTO dto) {
		return usuarioService.cambiarPassword(id, dto);
	}
	@PutMapping("/aprobar/{id}")
	public Usuario aprobarUsuario(
	        @PathVariable int id){

	    return usuarioService.aprobarUsuario(id);
	}
	/*
	@PutMapping("/rechazar/{id}")
	public Usuario rechazarUsuario(
	        @PathVariable int id){

	    return usuarioService.rechazarUsuario(id);
	}
	@DeleteMapping("/eliminar/{id}")
	public void eliminarSolicitud(
	        @PathVariable Integer id){

	    usuarioService.eliminarSolicitud(id);
	}	
	@GetMapping("/solicitudes")
	public List<SolicitudDTO> solicitudes() {

	    return usuarioService.listarSolicitudes();
	}
	*/
	@GetMapping("/dni/{dni}")
	public Usuario buscarPorDni(@PathVariable String dni) {

	    return usuarioService.buscarPorDni(dni);

	}
}
