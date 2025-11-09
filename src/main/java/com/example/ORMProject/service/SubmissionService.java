package com.example.ORMProject.service;

import com.example.ORMProject.model.Assignment;
import com.example.ORMProject.model.Submission;
import com.example.ORMProject.model.User;
import com.example.ORMProject.model.Role;
import com.example.ORMProject.repository.AssignmentRepository;
import com.example.ORMProject.repository.SubmissionRepository;
import com.example.ORMProject.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;

    public SubmissionService(SubmissionRepository submissionRepository,
                             AssignmentRepository assignmentRepository,
                             UserRepository userRepository) {
        this.submissionRepository = submissionRepository;
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Submission submit(Long studentId, Long assignmentId, String content, Long actorUserId) {
        User actor = userRepository.findById(actorUserId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден: id=" + actorUserId));
        if (actor.getRole() == Role.STUDENT && !actor.getId().equals(studentId)) {
            throw new IllegalStateException("Студент может отправлять решения только от своего имени.");
        }
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Студент не найден: id=" + studentId));
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Задание не найдено: id=" + assignmentId));

        submissionRepository.findByStudent_IdAndAssignment_Id(studentId, assignmentId)
                .ifPresent(s -> { throw new IllegalStateException("Повторная сдача не разрешена (по условию)."); });

        Submission s = new Submission();
        s.setAssignment(assignment);
        s.setStudent(student);
        s.setContent(content);
        s.setSubmittedAt(LocalDateTime.now());
        return submissionRepository.save(s);
    }

    @Transactional
    public Submission grade(Long submissionId, Long actorUserId, Integer score, String feedback) {
        User actor = userRepository.findById(actorUserId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден: id=" + actorUserId));
        if (actor.getRole() == Role.STUDENT) {
            throw new IllegalStateException("Оценивать может только преподаватель/администратор.");
        }
        Submission s = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new EntityNotFoundException("Решение не найдено: id=" + submissionId));
        s.setScore(score);
        s.setFeedback(feedback);
        return submissionRepository.save(s);
    }
}
