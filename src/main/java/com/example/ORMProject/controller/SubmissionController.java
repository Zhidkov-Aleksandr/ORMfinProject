package com.example.ORMProject.controller;

import com.example.ORMProject.model.Submission;
import com.example.ORMProject.repository.SubmissionRepository;
import com.example.ORMProject.service.SubmissionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Контроллер отправки и проверки домашних работ.

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;
    private final SubmissionRepository submissionRepository;

    public SubmissionController(SubmissionService submissionService,
                                SubmissionRepository submissionRepository) {
        this.submissionService = submissionService;
        this.submissionRepository = submissionRepository;
    }

    // Список решений по заданию.

    @GetMapping("/by-assignment/{assignmentId}")
    public List<Submission> byAssignment(@PathVariable Long assignmentId) {
        return submissionRepository.findByAssignment_Id(assignmentId);
    }

    // Список решений студента.

    @GetMapping("/by-student/{studentId}")
    public List<Submission> byStudent(@PathVariable Long studentId) {
        return submissionRepository.findByStudent_Id(studentId);
    }

    // Отправить решение (student).  Пример: POST /api/submissions/submit?studentId=7&assignmentId=12&actorUserId=7
    // Тело: обычный text/plain или JSON с полем content (ниже для простоты content в query).

    @PostMapping("/submit")
    public Submission submit(@RequestParam Long studentId,
                             @RequestParam Long assignmentId,
                             @RequestParam String content,
                             @RequestParam Long actorUserId) {
        return submissionService.submit(studentId, assignmentId, content, actorUserId);
    }

    // Оценить решение (teacher/admin). Пример: PUT /api/submissions/grade/15?actorUserId=3&score=90&feedback=OK

    @PutMapping("/grade/{submissionId}")
    public Submission grade(@PathVariable Long submissionId,
                            @RequestParam Long actorUserId,
                            @RequestParam Integer score,
                            @RequestParam(required = false) String feedback) {
        return submissionService.grade(submissionId, actorUserId, score, feedback);
    }

    @DeleteMapping("/{submissionId}")
    public ResponseEntity<Void> delete(@PathVariable Long submissionId) {
        submissionRepository.deleteById(submissionId);
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
