package com.SistemaAlquiler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class CulqiConfig {

@Value("${culqi.public.key}")
private String publicKey;

@Value("${culqi.secret.key}")
private String secretKey;

}