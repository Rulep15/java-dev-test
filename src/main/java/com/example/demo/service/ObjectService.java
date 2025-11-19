package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.client.ExternalApiClient;
import com.example.demo.repository.ObjectRepository;
import com.example.demo.dto.ObjectResponseDto;
import com.example.demo.entity.ObjectEntity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    public ObjectResponseDto getObject(String id) throws Exception {
        return repository.findById(id)
                .map(e -> new ObjectResponseDto(e.getId(), e.getName(), "LOCAL"))
                .orElseGet(() -> {
                    try {
                        String json = externalClient.getExternalObject(id);
                        JsonNode node = mapper.readTree(json);
                        String name = node.has("name") ? node.get("name").asText() : null;

                        return new ObjectResponseDto(id, name, "REMOTE");
                    } catch (Exception e) {
                        throw new RuntimeException("Error requesting external API");
                    }
                });
    }
}
