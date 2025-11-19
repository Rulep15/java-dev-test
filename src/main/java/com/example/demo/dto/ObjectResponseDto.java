package com.example.demo.dto;

public class ObjectResponseDto {

    private String id;
    private String name;
    private String source;

    public ObjectResponseDto(String id, String name, String source) {
        this.id = id;
        this.name = name;
        this.source = source;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSource() { return source; }
}

