package com.SistemaAlquiler.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class DocumentoDTO {

    private String tipdoc;
    private Integer codinq;
    private Integer codusu;

    private MultipartFile file;
}
