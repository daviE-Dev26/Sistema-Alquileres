package com.SistemaAlquiler.entity;

import java.io.Serializable;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class InquilinoCuartoId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer inquilino;
    private Integer cuarto;
}