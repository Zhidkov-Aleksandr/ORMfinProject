package com.example.ORMProject.service;

import com.example.ORMProject.model.Assignment;
import com.example.ORMProject.model.Lesson;
import com.example.ORMProject.model.Role;
import com.example.ORMProject.model.User;
import com.example.ORMProject.repository.AssignmentRepository;
import com.example.ORMProject.repository.LessonRepository;
import com.example.ORMProject.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    public AssignmentService(AssignmentRepository assignmentRepository,
                             LessonRepository lessonRepository,
                             UserRepository userRepository) {
        this.assignmentRepository = assignmentRepository;
        this.lessonRepository = lessonRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Assignment createAssignment(Long lessonId,
                                       Long actorUserId,
                                       String title,
                                       String description,
                                       LocalDate dueDate,
                                       Integer maxScore) {
        User actor = userRepository.findById(actorUserId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден: id=" + actorUserId));
        if (actor.getRole() == Role.STUDENT) {
            throw new IllegalStateException("Создавать задания может только преподаватель/администратор.");
        }
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityNotFoundException("Урок не найден: id=" + lessonId));

        Assignment a = new Assignment();
        a.setLesson(lesson);
        a.setTitle(title);
        a.setDescription(description);
        a.setDueDate(dueDate);
        a.setMaxScore(maxScore);
        return assignmentRepository.save(a);
    }

    @Transactional
    public Assignment updateAssignment(Long assignmentId,
                                       Long actorUserId,
                                       String newTitle,
                                       String newDescription,
                                       LocalDate newDueDate,
                                       Integer newMaxScore) {
        User actor = userRepository.findById(actorUserId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден: id=" + actorUserId));
        if (actor.getRole() == Role.STUDENT) {
            throw new IllegalStateException("Изменять задания может только преподаватель/администратор.");
        }
        Assignment a = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Задание не найдено: id=" + assignmentId));

        if (newTitle != null) a.setTitle(newTitle);
        if (newDescription != null) a.setDescription(newDescription);
        if (newDueDate != null) a.setDueDate(newDueDate);
        if (newMaxScore != null) a.setMaxScore(newMaxScore);

        return assignmentRepository.save(a);
    }

    @Transactional
    public void deleteAssignment(Long assignmentId, Long actorUserId) {
        User actor = userRepository.findById(actorUserId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден: id=" + actorUserId));
        if (actor.getRole() == Role.STUDENT) {
            throw new IllegalStateException("Удалять задания может только преподаватель/администратор.");
        }
        Assignment a = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Задание не найдено: id=" + assignmentId));
        assignmentRepository.delete(a);
    }
}
