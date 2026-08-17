package com.SistemaAlquiler.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SistemaAlquiler.dto.TipoDocumentoDTO;
import com.SistemaAlquiler.entity.TipoDocumento;
import com.SistemaAlquiler.repository.TipoDocumentoRepository;

@Service
public class TipoDocumentoService {
	
	@Autowired
	private TipoDocumentoRepository tipoDocumentoRepository;
	
	public List<TipoDocumento> findAll(){
		return tipoDocumentoRepository.findAll();
	}
	
	public TipoDocumento findById(int id) {
        return tipoDocumentoRepository.findById(id).orElse(null);
    }
	
	public TipoDocumento create(TipoDocumentoDTO dto) {

		TipoDocumento tipoDocumento = TipoDocumento.builder()
                .nomtipdoc(dto.getNomtipdoc())
                .esttipdoc(dto.getEsttipdoc())
                .build();

        return tipoDocumentoRepository.save(tipoDocumento);
    }
	
	public TipoDocumento update(int id, TipoDocumentoDTO dto) {

		TipoDocumento tipoDocumento = tipoDocumentoRepository.findById(id).orElse(null);

        if (tipoDocumento == null) return null;

        tipoDocumento.setNomtipdoc(dto.getNomtipdoc());
        tipoDocumento.setEsttipdoc(dto.getEsttipdoc());

        return tipoDocumentoRepository.save(tipoDocumento);
    }
	
	public void cambiarEstado(int id, boolean estado) {
		TipoDocumento tipoDocumento = tipoDocumentoRepository.findById(id).orElse(null);

        if (tipoDocumento == null) return;

        tipoDocumento.setEsttipdoc(estado);

        tipoDocumentoRepository.save(tipoDocumento);
    }
	

}
