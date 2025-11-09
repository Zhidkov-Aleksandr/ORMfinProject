package com.example.ORMProject.repository;

import com.example.ORMProject.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    // Найти все записи на курсы для конкретного студента
    List<Enrollment> findByStudent_Id(Long studentId);

    // Найти всех студентов, записанных на определенный курс
    List<Enrollment> findByCourse_Id(Long courseId);

    // Опционально: проверить существует ли запись
    boolean existsByStudent_IdAndCourse_Id(Long studentId, Long courseId);
}
