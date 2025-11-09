package com.example.ORMProject.service;

import com.example.ORMProject.model.Module;
import com.example.ORMProject.model.Quiz;
import com.example.ORMProject.model.Question;
import com.example.ORMProject.model.AnswerOption;
import com.example.ORMProject.model.Role;
import com.example.ORMProject.model.User;
import com.example.ORMProject.repository.ModuleRepository;
import com.example.ORMProject.repository.QuizRepository;
import com.example.ORMProject.repository.QuestionRepository;
import com.example.ORMProject.repository.AnswerOptionRepository;
import com.example.ORMProject.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class QuizService {

    private final QuizRepository quizRepository;
    private final ModuleRepository moduleRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final UserRepository userRepository;

    public QuizService(QuizRepository quizRepository,
                       ModuleRepository moduleRepository,
                       QuestionRepository questionRepository,
                       AnswerOptionRepository answerOptionRepository,
                       UserRepository userRepository) {
        this.quizRepository = quizRepository;
        this.moduleRepository = moduleRepository;
        this.questionRepository = questionRepository;
        this.answerOptionRepository = answerOptionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Quiz createQuizForModule(Long moduleId, Long actorUserId, String title) {
        User actor = userRepository.findById(actorUserId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден: id=" + actorUserId));
        if (actor.getRole() == Role.STUDENT) {
            throw new IllegalStateException("Создавать тест может только преподаватель/администратор.");
        }
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new EntityNotFoundException("Модуль не найден: id=" + moduleId));

        if (module.getQuiz() != null) {
            throw new IllegalStateException("У модуля уже есть тест.");
        }
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setModule(module);
        module.setQuiz(quiz);
        return quizRepository.save(quiz);
    }

    @Transactional
    public Question addQuestion(Long quizId, Long actorUserId, String text, String type) {
        User actor = userRepository.findById(actorUserId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден: id=" + actorUserId));
        if (actor.getRole() == Role.STUDENT) {
            throw new IllegalStateException("Добавлять вопросы может только преподаватель/администратор.");
        }
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Тест не найден: id=" + quizId));
        Question q = new Question();
        q.setQuiz(quiz);
        q.setText(text);
        q.setType(type);
        return questionRepository.save(q);
    }

    @Transactional
    public AnswerOption addAnswerOption(Long questionId, Long actorUserId, String text, boolean isCorrect) {
        User actor = userRepository.findById(actorUserId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден: id=" + actorUserId));
        if (actor.getRole() == Role.STUDENT) {
            throw new IllegalStateException("Добавлять варианты ответов может только преподаватель/администратор.");
        }
        Question q = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Вопрос не найден: id=" + questionId));
        AnswerOption opt = new AnswerOption();
        opt.setQuestion(q);
        opt.setText(text);
        opt.setCorrect(isCorrect);
        return answerOptionRepository.save(opt);
    }
}
