package com.example.ORMProject.controller;

import com.example.ORMProject.model.Assignment;
import com.example.ORMProject.repository.AssignmentRepository;
import com.example.ORMProject.service.AssignmentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

// Контроллер для CRUD по заданиям.

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final AssignmentRepository assignmentRepository;

    public AssignmentController(AssignmentService assignmentService,
                                AssignmentRepository assignmentRepository) {
        this.assignmentService = assignmentService;
        this.assignmentRepository = assignmentRepository;
    }

    // Список заданий по курсу (через связки lesson -> module -> course).

    @GetMapping("/by-course/{courseId}")
    public List<Assignment> byCourse(@PathVariable Long courseId) {
        return assignmentRepository.findByLesson_Module_Course_Id(courseId);
    }

    // Создать задание для урока (преподаватель/админ). Пример:
    // POST /api/assignments?lessonId=5&actorUserId=3&title=HW1&dueDate=2025-11-30&maxScore=100

    @PostMapping
    public Assignment create(@RequestParam Long lessonId,
                             @RequestParam Long actorUserId,
                             @RequestParam String title,
                             @RequestParam(required = false) String description,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
                             @RequestParam(required = false) Integer maxScore) {
        return assignmentService.createAssignment(lessonId, actorUserId, title, description, dueDate, maxScore);
    }

    // Обновить задание (преподаватель/админ).

    @PutMapping("/{assignmentId}")
    public Assignment update(@PathVariable Long assignmentId,
                             @RequestParam Long actorUserId,
                             @RequestParam(required = false) String newTitle,
                             @RequestParam(required = false) String newDescription,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newDueDate,
                             @RequestParam(required = false) Integer newMaxScore) {
        return assignmentService.updateAssignment(assignmentId, actorUserId, newTitle, newDescription, newDueDate, newMaxScore);
    }

    // Удалить задание (преподаватель/админ).

    @DeleteMapping("/{assignmentId}")
    public ResponseEntity<Void> delete(@PathVariable Long assignmentId,
                                       @RequestParam Long actorUserId) {
        assignmentService.deleteAssignment(assignmentId, actorUserId);
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
