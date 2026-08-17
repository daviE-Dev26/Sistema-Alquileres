package com.SistemaAlquiler.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; // ✅ Importación añadida para manejar respuestas estructuradas
import org.springframework.web.bind.annotation.*;

import com.SistemaAlquiler.dto.ActualizarInquilinoDTO;
import com.SistemaAlquiler.dto.ContratoFinalizadoDTO;
import com.SistemaAlquiler.dto.InquilinoListadoDTO;
import com.SistemaAlquiler.dto.usuario.CrearInquilinoDTO;
import com.SistemaAlquiler.entity.Inquilino;
import com.SistemaAlquiler.service.InquilinoService;

import com.SistemaAlquiler.entity.InquilinoHistorico;
import com.SistemaAlquiler.entity.InquilinoCuartoHistorico;


@RestController
@RequestMapping("/inquilino")
@CrossOrigin(origins = "*")
public class InquilinoController {
    @Autowired
    private InquilinoService inquilinoService;
    
    @GetMapping
    public List<Inquilino> listar() {
        return inquilinoService.findAll();
    }
    @GetMapping("/sede/{codsede}")
    public List<InquilinoListadoDTO> listarPorSede(
            @PathVariable Integer codsede){
        return inquilinoService
                .listarPorSede(codsede);
    }
    
    @GetMapping("/piso/{codpiso}")
    public List<InquilinoListadoDTO> listarPorPiso(
            @PathVariable Integer codpiso){
        return inquilinoService
                .listarPorPiso(codpiso);
    }
    
    @GetMapping("/cuarto/{codcuar}")
    public List<InquilinoListadoDTO> listarPorCuarto(
            @PathVariable Integer codcuar){

        return inquilinoService
                .listarPorCuarto(codcuar);
    }
    // ✅ MODIFICADO: Captura la excepción de duplicados y envía la respuesta HTTP 400 a Angular
    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody CrearInquilinoDTO dto) {
        try {
            Inquilino nuevoInquilino = inquilinoService.registrar(dto);
            return ResponseEntity.ok(nuevoInquilino);
        } catch (RuntimeException e) {
            // Envía el mensaje: "Ya tienes a un inquilino registrado con este DNI."
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // ✅ CORREGIDO: Retorna la lista con la estructura DTO requerida por el Front y llama al método correcto
    @GetMapping("/dashboard")
    public List<InquilinoListadoDTO> listarDashboard(@RequestParam int codusu) {
        return inquilinoService.listarDashboard(codusu);
    }
    // =========================================================
    // 📝 NUEVO: Endpoint para ACTUALIZAR un inquilino existente
    // =========================================================
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable int id, @RequestBody CrearInquilinoDTO dto) {
        try {
            // Pasamos el ID capturado de la URL y el DTO con los datos actualizados al Service
            inquilinoService.actualizar(id, dto);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            // Retorna un HTTP 400 con el mensaje de error controlado en caso falle
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // =========================================================
    // ❌ NUEVO: Endpoint para ELIMINAR (o desvincular) un inquilino
    // =========================================================
    @PutMapping("/{codasig}/finalizar")
    public ResponseEntity<?> finalizarContrato(
            @PathVariable Integer codasig){
        inquilinoService.finalizarContrato(codasig);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/{codasig}/reanudar")
    public ResponseEntity<?> reanudarContrato(
            @PathVariable Integer codasig){
        try{
            inquilinoService.reanudarContrato(codasig);
            return ResponseEntity.ok().build();
        }catch(RuntimeException e){
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }
    @GetMapping("/contratos-finalizados")
    public List<ContratoFinalizadoDTO>
    listarContratosFinalizados(
            @RequestParam int codusu){
        return inquilinoService
                .listarContratosFinalizados(codusu);
    }
    @GetMapping("/{codasig}")
    public CrearInquilinoDTO obtener(
            @PathVariable Integer codasig){
        return inquilinoService.obtener(codasig);
    }
    @GetMapping("/dashboard/reservas/{codusu}")
	public ResponseEntity<List<InquilinoListadoDTO>> listarReservas(@PathVariable int codusu) {
	    return ResponseEntity.ok(inquilinoService.listarReservas(codusu));
	}
    @PutMapping("/{codinq}/contacto")
    public ResponseEntity<?> actualizarInquilinoContacto(@PathVariable Integer codinq, @RequestBody ActualizarInquilinoDTO dto){
    	try {
    		return ResponseEntity.ok(inquilinoService.actualizarInquilinoContacto(codinq, dto));
    	}catch(RuntimeException e){
    		return ResponseEntity.badRequest().body(e.getMessage());   		
    	}
    }
    
    //nuevo
    @GetMapping("/historico-datos")
    public ResponseEntity<List<InquilinoHistorico>> listarHistoricoDatos(
            @RequestParam String dni,
            @RequestParam Integer codusu) {

        List<InquilinoHistorico> historial =
                inquilinoService.listarHistoricoDatos(dni, codusu);

        return ResponseEntity.ok(historial);
    }
    @GetMapping("/historico-cuartos")
    public ResponseEntity<List<InquilinoCuartoHistorico>> listarHistoricoCuartos(
            @RequestParam String dni,
            @RequestParam Integer codusu) {

        List<InquilinoCuartoHistorico> historial =
                inquilinoService.listarHistoricoCuartos(dni, codusu);

        return ResponseEntity.ok(historial);
    }
    
    @GetMapping("/historico-datos/todos")
    public ResponseEntity<List<InquilinoHistorico>> listarHistoricoDatosTodos(@RequestParam Integer codusu){
    	return ResponseEntity.ok(inquilinoService.listarTodosHistoricos(codusu));
    }
    
    @GetMapping("/historico-cuartos/todos")
    public ResponseEntity<List<InquilinoCuartoHistorico>> listarHistoricoCuartoAll(
            @RequestParam Integer codusu) {

        return ResponseEntity.ok(
                inquilinoService.listarTodosHistoricosCuartos(codusu)
        );
    }
    	
    
}