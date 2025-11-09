package com.example.ORMProject.repository;


import com.example.ORMProject.model.Course;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    // Пример дополнительного метода: поиск курсов по категории
    List<Course> findByCategory_Name(String categoryName);

    @EntityGraph(attributePaths = {"modules"})
    Optional<Course> findWithModulesById(Long id);
}
