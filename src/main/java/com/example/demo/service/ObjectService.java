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

    public ObjectResponseDto saveObject(String id) throws Exception {
        String json = externalClient.getExternalObject(id);

        JsonNode node = mapper.readTree(json);
        String name = node.has("name") ? node.get("name").asText() : null;

        ObjectEntity entity = new ObjectEntity(id, name, json);
        repository.save(entity);

        return new ObjectResponseDto(id, name, "LOCAL");
    }

    public ObjectResponseDto getObject(String id) {

    return repository.findById(id)
            .map(e -> new ObjectResponseDto(e.getId(), e.getName(), "LOCAL"))
            .orElseGet(() -> {

                try {
                    // Llamar a la API externa
                    String json = externalClient.getExternalObject(id);
                    JsonNode node = mapper.readTree(json);

                    // Si el JSON no tiene "name", devolver null
                    String name = node.has("name") ? node.get("name").asText() : null;

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
