package com.SistemaAlquiler.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SistemaAlquiler.entity.Documento;

public interface DocumentoRepository extends JpaRepository<Documento,Integer>{

	List<Documento> findByInquilinoCodinqAndTipdocAndEstdocTrue(Integer codinq, String tipdoc);

	List<Documento> findByInquilinoCodinq(Integer codinq);
	
	List<Documento> findByInquilinoCodinqAndTipdoc(Integer codinq, String tipdoc);

	void deleteByInquilinoCodinq(int id);
	
	
}