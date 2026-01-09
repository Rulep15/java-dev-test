package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;

import com.example.demo.service.ObjectService;
import com.example.demo.dto.ObjectResponseDto;

@RestController  //Aca lo que establece el restcontroller es que todo lo que traigas me devuelvas como JSON
@RequestMapping("/api/objects") //Todos los endpoint empiezan con esta clase 
public class ObjectController {

    private final ObjectService service; //Define una variable con el nombre service que toma de mi import de mi clase de object

    public ObjectController(ObjectService service) {
        this.service = service;
    }
//ACA CONFIGURO LAS PETICIONES 
    @PostMapping("/{id}")
    //pathvariable hace que guarde ese id de mi url dentro de la variable id
    public ObjectResponseDto save(@PathVariable String id) throws Exception { 
        return service.saveObject(id); //envia eso al service y el hace el resto
    }

    @GetMapping("/{id}")
    public ObjectResponseDto get(@PathVariable String id) throws Exception {
        return service.getObject(id);
    }
}
