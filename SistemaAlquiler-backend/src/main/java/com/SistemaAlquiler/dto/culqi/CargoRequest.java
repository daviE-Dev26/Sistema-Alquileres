package com.SistemaAlquiler.dto.culqi;

import java.util.Map;

import lombok.Data;

@Data
public class CargoRequest{

private Integer amount;
private String currency_code;
private String email;
private String source_id;
private Boolean capture;
private String description;
private Integer installments;
private Map<String,Object> metadata;
private AntifraudDetails antifraud_details;
}
