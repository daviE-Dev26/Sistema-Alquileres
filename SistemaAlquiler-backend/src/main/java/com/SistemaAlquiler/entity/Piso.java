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
import lombok.Data;

@Entity
@Data
public class Piso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codpiso;

    private Integer numero;

    private Boolean estado;

    @ManyToOne
    @JoinColumn(name = "codsede")
    private Sede sede;
    @JsonIgnore
    @OneToMany(mappedBy = "piso")
    private List<Cuarto> cuartos;
}