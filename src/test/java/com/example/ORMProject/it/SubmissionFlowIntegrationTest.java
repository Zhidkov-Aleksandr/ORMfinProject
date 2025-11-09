package com.example.ORMProject.it;

import com.example.ORMProject.model.*;
import com.example.ORMProject.model.Module;
import com.example.ORMProject.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;

 // Сквозной сценарий:
 // - создаём курс/модуль/урок/задание
 // - создаём студента
 // - студент отправляет решение
 // - преподватель выставляет оценку


public class SubmissionFlowIntegrationTest extends BasePostgresIT {

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ModuleRepository moduleRepository;
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private AssignmentRepository assignmentRepository;
    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    void testAssignmentSubmissionAndGrading() {
        // 1) Учебная структура
        Course course = new Course();
        course.setTitle("Алгоритмы");
        Module module = new Module();
        module.setTitle("Сортировки");
        module.setCourse(course);
        course.getModules().add(module);
        courseRepository.save(course);

        Lesson lesson = new Lesson();
        lesson.setTitle("Пузырьковая сортировка");
        lesson.setModule(module);
        lessonRepository.save(lesson);

        Assignment a = new Assignment();
        a.setLesson(lesson);
        a.setTitle("Реализовать Bubble Sort");
        a.setDescription("Код + пояснение");
        a.setDueDate(LocalDate.now().plusDays(7));
        a.setMaxScore(100);
        assignmentRepository.save(a);

        // 2) Студент
        User student = new User();
        student.setName("Студент");
        student.setEmail("student@example.com");
        student.setPassword("{noop}pwd"); // если используешь BCrypt — положи уже закодированный хэш
        student.setRole(Role.STUDENT);
        userRepository.save(student);

        // 3) Отправка решения
        Submission s = new Submission();
        s.setAssignment(a);
        s.setStudent(student);
        s.setSubmittedAt(LocalDateTime.now());
        s.setContent("Моё решение: ...");
        submissionRepository.save(s);

        Long submissionId = s.getId();
        Assertions.assertNotNull(submissionId, "Submission должен сохраниться");

        // 4) Преподаватель/проверка (для простоты — проставим оценку прямо здесь)
        Submission toGrade = submissionRepository.findById(submissionId).orElseThrow();
        toGrade.setScore(95);
        toGrade.setFeedback("Хорошая работа!");
        submissionRepository.save(toGrade);

        // 5) Проверяем
        Submission graded = submissionRepository.findById(submissionId).orElseThrow();
        Assertions.assertEquals(95, graded.getScore());
        Assertions.assertEquals("Хорошая работа!", graded.getFeedback());
    }
}
