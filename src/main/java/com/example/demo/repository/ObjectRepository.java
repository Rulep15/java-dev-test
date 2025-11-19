package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.ObjectEntity;

public interface ObjectRepository extends JpaRepository<ObjectEntity, String> {
}
