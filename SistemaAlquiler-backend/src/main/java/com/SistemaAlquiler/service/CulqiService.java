package com.SistemaAlquiler.service;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.SistemaAlquiler.config.CulqiConfig;
import com.SistemaAlquiler.dto.ConfirmarPagoDTO;
import com.SistemaAlquiler.dto.culqi.AntifraudDetails;
import com.SistemaAlquiler.dto.culqi.CargoRequest;
import com.SistemaAlquiler.dto.culqi.CargoResponse;

import tools.jackson.databind.ObjectMapper;

import org.springframework.web.client.HttpClientErrorException;
/*import org.springframework.http.HttpStatusCode;*/

@Service
public class CulqiService {

@Autowired
private CulqiConfig culqiConfig;
@Autowired
private RestTemplate restTemplate;

public CargoResponse crearCargo(
        ConfirmarPagoDTO dto,
        Double monto,
        String email){
    System.out.println("ENTRO A CULQI SERVICE");

    System.out.println(
        "SECRET KEY: "
        + culqiConfig.getSecretKey()
    );
    System.out.println("Monto recibido: " + monto);
    CargoRequest request =
            new CargoRequest();

    request.setAmount(
            (int)(monto * 100)
    );
    System.out.println("Amount enviado: " + request.getAmount());
    request.setCurrency_code(
            "PEN"
    );

    request.setEmail(
            email
    );
    if(dto.getToken()==null || dto.getToken().isEmpty()){
        throw new RuntimeException(
            "Debe proporcionar un token de Culqi"
        );
    }
    request.setSource_id(
            dto.getToken()
    );

    request.setCapture(
            true
    );

    request.setDescription(
            "Pago de alquiler"
    );
    AntifraudDetails anti =
            new AntifraudDetails();

    anti.setFirst_name("Robert");
    anti.setLast_name("Perez");
    anti.setAddress("Av Peru 123");
    anti.setAddress_city("Lima");
    anti.setCountry_code("PE");
    anti.setPhone_number("999999999");

    request.setAntifraud_details(anti);
    request.setInstallments(0);

    request.setMetadata(new HashMap<>());
    HttpHeaders headers =
            new HttpHeaders();

    headers.setContentType(
            MediaType.APPLICATION_JSON
    );

    headers.setBearerAuth(
            culqiConfig.getSecretKey()
    );

    HttpEntity<CargoRequest> entity =
            new HttpEntity<>(
                    request,
                    headers
            );
    try{
    	ObjectMapper mapper = new ObjectMapper();
    	System.out.println(
    			mapper.writerWithDefaultPrettyPrinter()
    			.writeValueAsString(request)
    			);
    	System.out.println("===== REQUEST CULQI =====");
    	System.out.println("Amount: " + request.getAmount());
    	System.out.println("Currency: " + request.getCurrency_code());
    	System.out.println("Email: " + request.getEmail());
    	System.out.println("Source: " + request.getSource_id());
    	System.out.println("Description: " + request.getDescription());
    	System.out.println("=========================");
    	System.out.println(headers);
        ResponseEntity<CargoResponse> response =
        restTemplate.postForEntity(
        "https://api.culqi.com/v2/charges",
        entity,
        CargoResponse.class
        );

        return response.getBody();

    }catch(HttpClientErrorException ex){
    	System.out.println(ex.getStatusCode());
    	System.out.println(ex.getResponseBodyAsString());
        throw new RuntimeException(
        "Error al procesar el pago con Culqi: "
        +
        ex.getResponseBodyAsString()
        
        );

    }
    
}
}
