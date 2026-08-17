package com.SistemaAlquiler.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor 
@AllArgsConstructor 
@Data 
@Builder
@Entity
@Table(name="documento")
public class Documento {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer coddoc;
	
	private String tipdoc;
	private String nomdoc;
	private String rutadoc;
	private LocalDateTime fechasubida;
	private Boolean estdoc;
	
	@ManyToOne
	@JoinColumn(name="codinq")
	private Inquilino inquilino;
	
	@ManyToOne
	@JoinColumn(name="codusu")
	private Usuario usuario;	

}
