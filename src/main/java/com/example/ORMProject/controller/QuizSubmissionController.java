package com.example.ORMProject.controller;

import com.example.ORMProject.model.QuizSubmission;
import com.example.ORMProject.repository.QuizSubmissionRepository;
import com.example.ORMProject.service.QuizSubmissionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// Контроллер прохождений тестов.

@RestController
@RequestMapping("/api/quiz-submissions")
public class QuizSubmissionController {

    private final QuizSubmissionService quizSubmissionService;
    private final QuizSubmissionRepository quizSubmissionRepository;

    public QuizSubmissionController(QuizSubmissionService quizSubmissionService,
                                    QuizSubmissionRepository quizSubmissionRepository) {
        this.quizSubmissionService = quizSubmissionService;
        this.quizSubmissionRepository = quizSubmissionRepository;
    }

    // DTO для приёма ответов.

    public static class TakeQuizRequest {
        private Long studentId;
        private Long quizId;
        private Long actorUserId;
        private Map<Long, List<Long>> answers;

        public Long getStudentId() {
            return studentId;
        }

        public void setStudentId(Long studentId) {
            this.studentId = studentId;
        }

        public Long getQuizId() {
            return quizId;
        }

        public void setQuizId(Long quizId) {
            this.quizId = quizId;
        }

        public Long getActorUserId() {
            return actorUserId;
        }

        public void setActorUserId(Long actorUserId) {
            this.actorUserId = actorUserId;
        }

        public Map<Long, List<Long>> getAnswers() {
            return answers;
        }

        public void setAnswers(Map<Long, List<Long>> answers) {
            this.answers = answers;
        }
    }

    /**
     * Пройти тест (student).
     * Пример: POST /api/quiz-submissions/take (JSON в body)
     * {
     *   "studentId": 7,
     *   "quizId": 12,
     *   "actorUserId": 7,
     *   "answers": {
     *       "101": [1001],
     *       "102": [1005, 1006]
     *   }
     * }
     */
    @PostMapping("/take")
    public QuizSubmission take(@RequestBody TakeQuizRequest request) {
        return quizSubmissionService.takeQuiz(
                request.getStudentId(),
                request.getQuizId(),
                request.getAnswers(),
                request.getActorUserId()
        );
    }

    // Список результатов по тесту.

    @GetMapping("/by-quiz/{quizId}")
    public List<QuizSubmission> byQuiz(@PathVariable Long quizId) {
        return quizSubmissionRepository.findByQuiz_Id(quizId);
    }

    //  Список результатов по студену.

    @GetMapping("/by-student/{studentId}")
    public List<QuizSubmission> byStudent(@PathVariable Long studentId) {
        return quizSubmissionRepository.findByStudent_Id(studentId);
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
