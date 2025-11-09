package com.example.ORMProject.repository;

import com.example.ORMProject.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Поиск категории по названию
    Category findByName(String name);
}
