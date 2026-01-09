package com.example.demo.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.client.ExternalApiClient;
import com.example.demo.repository.ObjectRepository;
import com.example.demo.dto.ObjectResponseDto;
import com.example.demo.entity.ObjectEntity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class ObjectService {

    private final ExternalApiClient externalClient;
    private final ObjectRepository repository;
    private final ObjectMapper mapper = new ObjectMapper();

    public ObjectService(ExternalApiClient externalClient, ObjectRepository repository) {
        this.externalClient = externalClient;
        this.repository = repository;
    }


    //Este metodo es el que se utiliza al realizar un POST
    public ObjectResponseDto saveObject(String id) throws Exception {
        String json = externalClient.getExternalObject(id); //Este llama a la api externa que esta en mi client

        JsonNode node = mapper.readTree(json); //Convertir el texto a un objeto 
        String name = node.has("name") ? node.get("name").asText() : null; //Del json extrae el campo name

        ObjectEntity entity = new ObjectEntity(id, name, json);
        repository.save(entity); //guarda en la base datos local H2

        return new ObjectResponseDto(id, name, "LOCAL");  //Aca devuelve siempre el Source en LOCAL
    }


 //Este metodo es el que se utiliza al realizar un GET
    public ObjectResponseDto getObject(String id) {

    return repository.findById(id)
            .map(e -> new ObjectResponseDto(e.getId(), e.getName(), "LOCAL")) //En el caso de que exista en mi base lo que hace es mandar como LOCAL
            .orElseGet(() -> { //LANDA

                try {
                    // Llamar a la API externa
                    String json = externalClient.getExternalObject(id);
                    
                    //Lee el Json
                    JsonNode node = mapper.readTree(json);

                    // Extrae del JSON el name
                    String name = node.has("name") ? node.get("name").asText() : null;

                    //Devuelve el DTO con source "REMOTE"
                    return new ObjectResponseDto(id, name, "REMOTE");

                } catch (HttpClientErrorException.NotFound e) {

                    // El API externo devolvió 404 → devolver 404 a tu cliente
                    throw new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Object with id " + id + " was not found in the external API."
                    );

                } catch (Exception e) {

                    // Cualquier otro error → devolver 500
                    throw new ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Error requesting external API"
                    );
                }
            });
}

}
