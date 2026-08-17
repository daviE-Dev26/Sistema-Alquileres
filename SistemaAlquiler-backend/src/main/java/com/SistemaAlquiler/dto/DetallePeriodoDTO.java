package com.SistemaAlquiler.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetallePeriodoDTO {
    private Integer anio;
    private Integer mes;
    private String estado;
    private Double monto;

}
