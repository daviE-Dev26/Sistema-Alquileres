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
@Table(name="usuario")
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer codusu;
	
	private String nomusu;
    private String apepusu;
    private String apemusu;
    private String docusu;
    private String dirusu;
    private LocalDate  fecusu;
    private String celusu;
    private String corusu;
    private String passusu;

    private String  estusu;
    
    @ManyToOne
    @JoinColumn(name="codrol")
    private Rol rol;
    
    @ManyToOne
    @JoinColumn(name="codtipdoc")
    private TipoDocumento tipoDocumento;
}
