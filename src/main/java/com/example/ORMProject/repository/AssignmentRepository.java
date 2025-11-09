package com.example.ORMProject.repository;

import com.example.ORMProject.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    // Найти задания по курсу (через связь урок-модуль-курс)
    List<Assignment> findByLesson_Module_Course_Id(Long courseId);
}
