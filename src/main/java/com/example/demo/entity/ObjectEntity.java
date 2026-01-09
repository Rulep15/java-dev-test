package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
//Estructura de como es mi tabla 
@Entity
public class ObjectEntity {

    @Id
    private String id;

    private String name;

    @Column(length = 5000)
    private String rawJson;

    //Necesario para que Spring pueda crear objetos automáticamente
    public ObjectEntity() {}

    public ObjectEntity(String id, String name, String rawJson) {
        this.id = id;
        this.name = name;
        this.rawJson = rawJson;
    }
    //Leer los datos si queres sacar los datos usar estos metodos
    public String getId() { return id; }
    public String getName() { return name; }
    public String getRawJson() { return rawJson; }
    //Pára modificar valores
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setRawJson(String rawJson) { this.rawJson = rawJson; }
}

