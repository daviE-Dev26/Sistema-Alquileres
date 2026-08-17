package com.SistemaAlquiler.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name="sede")
public class Sede {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codsede;

    private String nombre;
    private String direccion;
    private String descripcion;
    private Boolean estado;

    @ManyToOne
    @JoinColumn(name = "codusu")
    private Usuario usuario;
    
    @JsonIgnore
    @OneToMany(mappedBy = "sede")
    private List<Piso> pisos;
}