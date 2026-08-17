package com.SistemaAlquiler.dto.culqi;

import lombok.Data;

@Data
public class CargoResponse{

private String id;
private String object;
private Integer amount;
private String currency_code;
private String description;
private Boolean capture;
private Boolean paid;
private String reference_code;
private String authorization_code;

}
