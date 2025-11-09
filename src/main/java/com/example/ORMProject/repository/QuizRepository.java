package com.example.ORMProject.repository;

import com.example.ORMProject.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    // можно добавить поиск теста по модулю
    Quiz findByModule_Id(Long moduleId);
}
