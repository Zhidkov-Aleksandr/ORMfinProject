package com.example.ORMProject.controller;

import com.example.ORMProject.model.Enrollment;
import com.example.ORMProject.service.EnrollmentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Контроллер записи студентов на курсы.

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    // Список записей на конкретный курс.

    @GetMapping("/by-course/{courseId}")
    public List<Enrollment> byCourse(@PathVariable Long courseId) {
        return enrollmentService.findByCourse(courseId);
    }

    // Список записей конкретного студента.

    @GetMapping("/by-student/{studentId}")
    public List<Enrollment> byStudent(@PathVariable Long studentId) {
        return enrollmentService.findByStudent(studentId);
    }

    // Записать студента на курс. Пример: POST /api/enrollments/enroll?courseId=10&studentId=7&actorUserId=7

    @PostMapping("/enroll")
    public Enrollment enroll(@RequestParam Long courseId,
                             @RequestParam Long studentId,
                             @RequestParam Long actorUserId) {
        return enrollmentService.enrollStudent(courseId, studentId, actorUserId);
    }

    // Отписать студента от курса. Пример: DELETE /api/enrollments/unenroll?courseId=10&studentId=7&actorUserId=7

    @DeleteMapping("/unenroll")
    public ResponseEntity<Void> unenroll(@RequestParam Long courseId,
                                         @RequestParam Long studentId,
                                         @RequestParam Long actorUserId) {
        enrollmentService.unenrollStudent(courseId, studentId, actorUserId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
