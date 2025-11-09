package com.example.ORMProject.service;

import com.example.ORMProject.model.*;
import com.example.ORMProject.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class QuizSubmissionService {

    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;

    public QuizSubmissionService(QuizRepository quizRepository,
                                 UserRepository userRepository,
                                 QuestionRepository questionRepository,
                                 AnswerOptionRepository answerOptionRepository,
                                 QuizSubmissionRepository quizSubmissionRepository) {
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.answerOptionRepository = answerOptionRepository;
        this.quizSubmissionRepository = quizSubmissionRepository;
    }

    /**
     * answers: Map<questionId, List<answerOptionId>>
     * Для SINGLE_CHOICE передавай список из одного варианта.
     */
    @Transactional
    public QuizSubmission takeQuiz(Long studentId, Long quizId, Map<Long, List<Long>> answers, Long actorUserId) {
        User actor = userRepository.findById(actorUserId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден: id=" + actorUserId));
        if (actor.getRole() == Role.STUDENT && !actor.getId().equals(studentId)) {
            throw new IllegalStateException("Студент может проходить тест только от своего имени.");
        }
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Студент не найден: id=" + studentId));
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Тест не найден: id=" + quizId));

        int total = quiz.getQuestions().size();
        int correct = 0;

        for (Question q : quiz.getQuestions()) {
            List<Long> selected = answers.getOrDefault(q.getId(), Collections.emptyList());
            if (selected.isEmpty()) continue;

            // Правильные варианты по вопросу
            List<AnswerOption> options = q.getOptions();
            Set<Long> correctIds = new HashSet<>();
            for (AnswerOption o : options) {
                if (o.isCorrect()) correctIds.add(o.getId());
            }
            Set<Long> selectedSet = new HashSet<>(selected);

            if (selectedSet.equals(correctIds)) {
                correct++;
            }
        }

        QuizSubmission sub = new QuizSubmission();
        sub.setQuiz(quiz);
        sub.setStudent(student);
        sub.setScore(correct); // можно хранить проценты при желании
        sub.setTakenAt(LocalDateTime.now());
        return quizSubmissionRepository.save(sub);
    }
}
