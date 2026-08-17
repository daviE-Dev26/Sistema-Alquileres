package com.SistemaAlquiler.service;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import com.SistemaAlquiler.dto.DocumentoDTO;
import com.SistemaAlquiler.entity.Documento;
import com.SistemaAlquiler.entity.Inquilino;
import com.SistemaAlquiler.entity.Usuario;
import com.SistemaAlquiler.repository.DocumentoRepository;
import com.SistemaAlquiler.repository.InquilinoRepository;
import com.SistemaAlquiler.repository.UsuarioRepository;

@Service
public class DocumentoService {

    @Autowired
    private DocumentoRepository documentoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private InquilinoRepository inquilinoRepository;

    public List<Documento> findAll() {
        return documentoRepository.findAll();
    }

    public Documento findById(int id) {
        return documentoRepository.findById(id).orElse(null);
    }

    public Documento create(DocumentoDTO dto) throws Exception {

        Inquilino inquilino = inquilinoRepository.findById(dto.getCodinq())
                .orElseThrow(() -> new RuntimeException("Inquilino no encontrado"));

        Usuario usuario = usuarioRepository.findById(dto.getCodusu())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String basePath = Paths.get(System.getProperty("user.dir"),
                "uploads",
                "inquilinos",
                dto.getCodinq().toString())
                .toAbsolutePath()
                .toString();

        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String original = dto.getFile().getOriginalFilename();
        String contentType = dto.getFile().getContentType();   
        String extension =".pdf";
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");
        
        String nombreArchivo =
                dto.getTipdoc().toLowerCase()
                + "_"
                + LocalDateTime.now().format(formatter)
                + extension;
        
        String ruta = basePath + File.separator + nombreArchivo;

        //existencia de archivo
        if (dto.getFile() == null || dto.getFile().isEmpty()) {
            throw new RuntimeException("Archivo vacío");
        }
        
        if(dto.getFile().getSize()>10*1024*1024) {
        	throw new RuntimeException("El archivo supera 10MB");
        }
        
        //nombre valido
        if(original == null || !original.toLowerCase().endsWith(".pdf")) {
        	throw new RuntimeException("Solo se permiten archivos PDF");
        }
        
        //validar MIME
        if (contentType == null ||
        		   (!contentType.equals("application/pdf")
        		    && !contentType.equals("application/octet-stream"))) {
        		    throw new RuntimeException("Solo PDF permitidos");
        		}
        
        try {
            byte[] bytes = dto.getFile().getBytes();
            if(bytes.length<4) {
            	throw new RuntimeException("Archivo invalido");
            }
            String header = new String(bytes, 0, 4);
            
            if (!"%PDF".equals(header)) {
                throw new RuntimeException("El archivo no es un PDF válido (contenido inválido)");
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Error al validar el archivo PDF");
        }
        
        
        
        dto.getFile().transferTo(new File(ruta));

        List<Documento> activos = documentoRepository
                .findByInquilinoCodinqAndTipdocAndEstdocTrue(
                        dto.getCodinq(),
                        dto.getTipdoc()
                );

        // 2. desactivar anteriores
        for (Documento d : activos) {
            d.setEstdoc(false);
        }

        // 3. guardar cambios en BD
        documentoRepository.saveAll(activos);

        
        
        Documento doc = Documento.builder()
                .tipdoc(dto.getTipdoc())
                .nomdoc(nombreArchivo)
                .rutadoc(ruta)
                .fechasubida(LocalDateTime.now())
                .estdoc(true)
                .inquilino(inquilino)
                .usuario(usuario)
                .build();

        return documentoRepository.save(doc);
    }

    public Documento update(int id, DocumentoDTO dto) throws Exception {

        Documento doc = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

        Inquilino inquilino = inquilinoRepository.findById(dto.getCodinq())
                .orElseThrow(() -> new RuntimeException("Inquilino no encontrado"));

        Usuario usuario = usuarioRepository.findById(dto.getCodusu())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (dto.getFile() != null && !dto.getFile().isEmpty()) {

            String basePath = Paths.get(System.getProperty("user.dir"),
                    "uploads",
                    "inquilinos",
                    dto.getCodinq().toString())
                    .toAbsolutePath()
                    .toString();

            File dir = new File(basePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String original = dto.getFile().getOriginalFilename();

            String extension = original.substring(original.lastIndexOf("."));

            String nombreArchivo = dto.getTipdoc().toLowerCase() + extension;

            String ruta = basePath + File.separator + nombreArchivo;

            dto.getFile().transferTo(new File(ruta));

            doc.setNomdoc(nombreArchivo);
            doc.setRutadoc(ruta);
        }

        doc.setTipdoc(dto.getTipdoc());
        doc.setInquilino(inquilino);
        doc.setUsuario(usuario);

        return documentoRepository.save(doc);
    }

    public Documento deshabilitar(int id) {
        Documento doc = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

        doc.setEstdoc(false);
        return documentoRepository.save(doc);
    }

    public Documento habilitar(int id) {
        Documento doc = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

        doc.setEstdoc(true);
        return documentoRepository.save(doc);
    }
    
    public Documento restaurar(int id) {
    	 Documento doc = documentoRepository.findById(id)
    	            .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

    	    List<Documento> otrosActivos = documentoRepository
    	            .findByInquilinoCodinqAndTipdocAndEstdocTrue(
    	                    doc.getInquilino().getCodinq(),
    	                    doc.getTipdoc()
    	            );

    	    for (Documento d : otrosActivos) {
    	        d.setEstdoc(false);
    	    }

    	    documentoRepository.saveAll(otrosActivos);

    	    doc.setEstdoc(true);

    	    return documentoRepository.save(doc);
    	   }
    
    public ResponseEntity<Resource> descargar(int id) throws MalformedURLException {

        Documento doc = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

        Path path = Paths.get(doc.getRutadoc());
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + doc.getNomdoc() + "\"")
                .body(resource);
    }
    
    public List<Documento> findByInquilinoAndTipo(int codinq, String tipdoc){
    	
    	if(tipdoc == null || tipdoc.isEmpty()) {
    		return documentoRepository.findByInquilinoCodinq(codinq);
    	}
    	
    	return documentoRepository.findByInquilinoCodinqAndTipdoc(codinq, tipdoc);
    	
    }
    
    public List<Documento> filtrar(int codinq, String tipdoc, Boolean activos) {

        List<Documento> docs = documentoRepository.findByInquilinoCodinq(codinq);

        if (tipdoc != null && !tipdoc.isEmpty()) {
            docs = docs.stream()
                    .filter(d -> d.getTipdoc().equalsIgnoreCase(tipdoc))
                    .toList();
        }

        if (activos != null && activos) {
            docs = docs.stream()
                    .filter(d -> Boolean.TRUE.equals(d.getEstdoc()))
                    .toList();
        }

        return docs;
    }
    
    
    
}