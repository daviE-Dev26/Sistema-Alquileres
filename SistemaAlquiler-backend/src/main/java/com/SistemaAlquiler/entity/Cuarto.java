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
@Table(name="cuarto")
public class Cuarto {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer codcuar;
	
	private Integer numcuar;
    private String passcuar;
    private String dircuar;
    private Double preccuar;
    private LocalDate feccuar;
    private String descuar;
    private String fotocuar;
    private String estcuar;
    private Boolean habilitado;
    @ManyToOne
    @JoinColumn(name="codusu")
    private Usuario usuario;
    @ManyToOne
    @JoinColumn(name="codsede")
    private Sede sede;
    @ManyToOne
    @JoinColumn(name = "codpiso")
    private Piso piso;

}
