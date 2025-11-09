package com.example.ORMProject.repository;

import com.example.ORMProject.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByModule_Course_Id(Long courseId);
}
