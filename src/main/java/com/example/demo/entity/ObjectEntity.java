package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
public class ObjectEntity {

    @Id
    private String id;

    private String name;

    @Column(length = 5000)
    private String rawJson;

    public ObjectEntity() {}

    public ObjectEntity(String id, String name, String rawJson) {
        this.id = id;
        this.name = name;
        this.rawJson = rawJson;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getRawJson() { return rawJson; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setRawJson(String rawJson) { this.rawJson = rawJson; }
}

