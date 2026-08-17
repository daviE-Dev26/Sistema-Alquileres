package com.SistemaAlquiler.entity;


import java.time.LocalDate;

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
@Table(name="servicio")
public class Servicio {

	 @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Integer codserv;
	 
	private String tipserv;
	private LocalDate feching;
	private Double monto;
	private String coment;
	
	@ManyToOne
	@JoinColumn(name="codusu")
	private Usuario usuario;
	
	@ManyToOne
	@JoinColumn(name="codsede")
	private Sede sede;
		
	
}
