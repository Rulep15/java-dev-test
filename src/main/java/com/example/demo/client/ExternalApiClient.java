package com.example.demo.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

//Aca con este component decimos que esta clase se puede usar tmb en otro lado
@Component
public class ExternalApiClient {

//Toma el valor de aplication poperties para saber la url de donde esta la base de datos externa y guarda en una variable baseUrl
    @Value("${external.api.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    
// Metodo para obtener los datos la base de datos externa
    public String getExternalObject(String id) {

        // Construimos la URL completa usando la baseUrl del archivo de configuración
        String url = baseUrl + "/objects/" + id;

        // Hacemos una petición HTTP GET a la URL externa usando RestTemplate
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // Devolvemos solamente el cuerpo de la respuesta, que contiene el JSON enviado por la API externa
        return response.getBody();
    }
}
