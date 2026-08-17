package com.SistemaAlquiler.controller;

import java.net.MalformedURLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.SistemaAlquiler.dto.DocumentoDTO;
import com.SistemaAlquiler.entity.Documento;
import com.SistemaAlquiler.service.DocumentoService;

@RestController
@RequestMapping("/documentos")
@CrossOrigin(origins = "*")
public class DocumentoController {

    @Autowired
    private DocumentoService documentoService;

    @GetMapping
    public List<Documento> listar() {
        return documentoService.findAll();
    }

    @GetMapping("/{id}")
    public Documento obtener(@PathVariable int id) {
        return documentoService.findById(id);
    }
    
    @GetMapping("/inquilino/{codinq}")
    public List<Documento> listarPorInquilino(@PathVariable int codinq) {
        return documentoService.findAll()
                .stream()
                .filter(d -> d.getInquilino().getCodinq() == codinq)
                .toList();
    }

    @PostMapping(value = "/create", consumes = "multipart/form-data")
    public ResponseEntity<Documento> create(@ModelAttribute DocumentoDTO dto) {
        try {
            Documento doc = documentoService.create(dto);
            return ResponseEntity.ok(doc);
        } catch (Exception e) {
        	 e.printStackTrace();
            return ResponseEntity.badRequest().build();
            
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Documento> update(
            @PathVariable int id,
            @ModelAttribute DocumentoDTO dto) {
        try {
            Documento doc = documentoService.update(id, dto);
            return ResponseEntity.ok(doc);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/disable/{id}")
    public Documento deshabilitar(@PathVariable int id) {
        return documentoService.deshabilitar(id);
    }

    @PutMapping("/restore/{id}")
    public ResponseEntity<Documento> restaurar(@PathVariable int id) {
    	 try {
    	        Documento doc = documentoService.restaurar(id);
    	        return ResponseEntity.ok(doc);
    	    } catch (Exception e) {
    	        return ResponseEntity.badRequest().build();
    	    }    }
    
    @GetMapping("/descargar/{id}")
    public ResponseEntity<Resource> descargar(@PathVariable int id) throws MalformedURLException {
        return documentoService.descargar(id);
    }
    
    @GetMapping("/inquilino/{codinq}/tipo")
    public List<Documento> listarPorTipo(
            @PathVariable int codinq,
            @RequestParam(required = false) String tipdoc) {

        return documentoService.findByInquilinoAndTipo(codinq, tipdoc);
    }
    
    @GetMapping("/inquilino/{codinq}/filtro")
    public List<Documento> filtrar(
            @PathVariable int codinq,
            @RequestParam(required = false) String tipdoc,
            @RequestParam(required = false) Boolean activos) {

        return documentoService.filtrar(codinq, tipdoc, activos);
    }
    
    
}