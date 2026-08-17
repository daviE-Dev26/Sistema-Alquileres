package com.SistemaAlquiler.dto;

import com.SistemaAlquiler.entity.MetodoPago;

import lombok.Data;

@Data
public class ConfirmarPagoDTO{
private Integer codasig;
private Integer cantidadMeses;
private MetodoPago metodoPago;
private String numeroOperacion;
private String observacion;
private String token;
}