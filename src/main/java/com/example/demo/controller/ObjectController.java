package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;

import com.example.demo.service.ObjectService;
import com.example.demo.dto.ObjectResponseDto;

@RestController
@RequestMapping("/api/objects")
public class ObjectController {

    private final ObjectService service;

    public ObjectController(ObjectService service) {
        this.service = service;
    }

    @PostMapping("/{id}")
    public ObjectResponseDto save(@PathVariable String id) throws Exception {
        return service.saveObject(id);
    }

    @GetMapping("/{id}")
    public ObjectResponseDto get(@PathVariable String id) throws Exception {
        return service.getObject(id);
    }
}
