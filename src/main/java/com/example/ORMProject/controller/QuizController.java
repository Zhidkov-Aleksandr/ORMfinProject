package com.example.ORMProject.controller;

import com.example.ORMProject.model.AnswerOption;
import com.example.ORMProject.model.Question;
import com.example.ORMProject.model.Quiz;
import com.example.ORMProject.service.QuizService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Контроллер для создания тестов, вопросов и вариантов ответов.

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    // Создать квиз на модуль (teacher/admin). Пимер: POST /api/quizzes?moduleId=4&actorUserId=3&title=Test%20for%20Module%201

    @PostMapping
    public Quiz createQuiz(@RequestParam Long moduleId,
                           @RequestParam Long actorUserId,
                           @RequestParam String title) {
        return quizService.createQuizForModule(moduleId, actorUserId, title);
    }

    // Добавить вопрос к квизу. Пример: POST /api/quizzes/{quizId}/questions?actorUserId=3&type=SINGLE_CHOICE&text=2+2=?

    @PostMapping("/{quizId}/questions")
    public Question addQuestion(@PathVariable Long quizId,
                                @RequestParam Long actorUserId,
                                @RequestParam String text,
                                @RequestParam String type) {
        return quizService.addQuestion(quizId, actorUserId, text, type);
    }

    // Добавить вариант ответа к вопросу. Пример: POST /api/quizzes/questions/{questionId}/options?actorUserId=3&text=4&isCorrect=true

    @PostMapping("/questions/{questionId}/options")
    public AnswerOption addOption(@PathVariable Long questionId,
                                  @RequestParam Long actorUserId,
                                  @RequestParam String text,
                                  @RequestParam boolean isCorrect) {
        return quizService.addAnswerOption(questionId, actorUserId, text, isCorrect);
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