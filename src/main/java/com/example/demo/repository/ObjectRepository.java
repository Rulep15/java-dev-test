package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.ObjectEntity;
//Esa línea crea un repositorio que puede guardar y buscar ObjectEntity en la base H2. esta vacio por que spring bot hace todo el resto
public interface ObjectRepository extends JpaRepository<ObjectEntity, String> {
}
