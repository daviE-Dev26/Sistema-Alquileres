package com.SistemaAlquiler.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.SistemaAlquiler.dto.CuartoDTO;
import com.SistemaAlquiler.dto.EditarCuartoDTO;
import com.SistemaAlquiler.dto.auth.LoginInquilinoRequestDTO;
import com.SistemaAlquiler.entity.Cuarto;
import com.SistemaAlquiler.service.CuartoService;

@RestController
@RequestMapping("/cuarto")
@CrossOrigin(origins = "*") // Asegura el intercambio de recursos con Angular
public class CuartoController {
	@Autowired
	private CuartoService cuartoService;
	// 🔐 NUEVO: Endpoint de Login para el Inquilino usando su 'passcuar'
	// Responde a: POST http://localhost:8080/cuarto/login-acceso
	@PostMapping("/login-acceso")
	public ResponseEntity<?> loginInquilino(@RequestBody LoginInquilinoRequestDTO request) {
		return ResponseEntity.ok(cuartoService.loginInquilino(request.getCodigo()));
	}
	// ✅ CORREGIDO: Ahora recibe el codusu como parámetro de consulta (?codusu=X)
	@GetMapping
	public List<CuartoDTO> findAll(@RequestParam int codusu) {
	    return cuartoService.listarCuartos(codusu);
	}
	@GetMapping("/{id}")
	public EditarCuartoDTO findById(@PathVariable int id) {
		return cuartoService.findById(id);
	}
	@GetMapping("/piso/{codpiso}")
	public List<CuartoDTO> listarPorPiso(
	@PathVariable Integer codpiso){
	    return cuartoService.listarCuartosPorPiso(codpiso);
	}
	// ✅ CORREGIDO: Ahora también filtra los disponibles por el propietario (?codusu=X)
	@GetMapping("/disponibles")
	public List<Cuarto> listarDisponibles(@RequestParam int codusu) {
		return cuartoService.listarDisponibles(codusu);
	}	
	//SEDE
	@GetMapping("/sede/{codsede}")
	public List<CuartoDTO> listarPorSede(
	        @PathVariable Integer codsede){
	    return cuartoService.listarCuartosPorSede(codsede);
	}
	@GetMapping("/disponibles/piso/{codpiso}")
	public List<Cuarto> listarDisponiblesPorPiso(
	        @PathVariable Integer codpiso){
	    return cuartoService.listarDisponiblesPorPiso(codpiso);
	}
	// ✅ MODIFICADO: Captura excepciones de duplicados y envía la respuesta correcta a Angular
	@PostMapping
	public ResponseEntity<?> create(@RequestBody CuartoDTO dto) {
		try {
			Cuarto nuevoCuarto = cuartoService.create(dto);
			return ResponseEntity.ok(nuevoCuarto);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	@PostMapping("/inteligente")
	public ResponseEntity<?> crearInteligente(@RequestBody CuartoDTO dto) {
	    try {
	        return ResponseEntity.ok(cuartoService.createInteligente(dto));
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body(e.getMessage());
	    }
	}
	@PutMapping("/{id}")
	public Cuarto update(@PathVariable int id,@RequestBody EditarCuartoDTO dto){
	    return cuartoService.update(id,dto);
	}
	@PutMapping("/{id}/estado")
	public void cambiarEstado(@PathVariable int id, @RequestParam String estado) {
		cuartoService.cambiarEstado(id, estado);
	}
	@PutMapping("/{id}/habilitado")
	public void cambiarHabilitado(@PathVariable int id, @RequestParam boolean estado){
	    cuartoService.cambiarHabilitado(id, estado);
	}
}