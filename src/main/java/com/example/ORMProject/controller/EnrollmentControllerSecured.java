package com.example.ORMProject.controller;

import com.example.ORMProject.model.Enrollment;
import com.example.ORMProject.security.AuthFacade;
import com.example.ORMProject.service.EnrollmentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Версия контроллера, в которой actorUserId берём из SecurityContext через AuthFacade
// Для работы требуется аутентификация (см. SecurityConfig).

@RestController
@RequestMapping("/api/secure/enrollments")
public class EnrollmentControllerSecured {

    private final EnrollmentService enrollmentService;
    private final AuthFacade authFacade;

    public EnrollmentControllerSecured(EnrollmentService enrollmentService,
                                       AuthFacade authFacade) {
        this.enrollmentService = enrollmentService;
        this.authFacade = authFacade;
    }

    @GetMapping("/by-course/{courseId}")
    public List<Enrollment> byCourse(@PathVariable Long courseId) {
        return enrollmentService.findByCourse(courseId);
    }

    @GetMapping("/by-student/{studentId}")
    public List<Enrollment> byStudent(@PathVariable Long studentId) {
        return enrollmentService.findByStudent(studentId);
    }

    // Запись на курс — actor берётся из SecurityContext. Пример: POST /api/secure/enrollments/enroll?courseId=10&studentId=7

    @PostMapping("/enroll")
    public Enrollment enroll(@RequestParam Long courseId,
                             @RequestParam Long studentId) {
        Long actorUserId = authFacade.currentUserId();
        return enrollmentService.enrollStudent(courseId, studentId, actorUserId);
    }

    // Отписка от курса — actor берётся из SecurityContext.

    @DeleteMapping("/unenroll")
    public ResponseEntity<Void> unenroll(@RequestParam Long courseId,
                                         @RequestParam Long studentId) {
        Long actorUserId = authFacade.currentUserId();
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
