package com.example.ORMProject.service;

import com.example.ORMProject.model.Course;
import com.example.ORMProject.model.Enrollment;
import com.example.ORMProject.model.User;
import com.example.ORMProject.model.Role;
import com.example.ORMProject.repository.CourseRepository;
import com.example.ORMProject.repository.EnrollmentRepository;
import com.example.ORMProject.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             UserRepository userRepository,
                             CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    public List<Enrollment> findByCourse(Long courseId) {
        return enrollmentRepository.findByCourse_Id(courseId);
    }

    public List<Enrollment> findByStudent(Long studentId) {
        return enrollmentRepository.findByStudent_Id(studentId);
    }

    @Transactional
    public Enrollment enrollStudent(Long courseId, Long studentId, Long actorUserId) {
        User actor = userRepository.findById(actorUserId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден: id=" + actorUserId));
        if (actor.getRole() == Role.STUDENT && !actor.getId().equals(studentId)) {
            throw new IllegalStateException("Студент может записывать только себя.");
        }
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Студент не найден: id=" + studentId));
        if (student.getRole() != Role.STUDENT && student.getRole() != Role.ADMIN) {
            throw new IllegalStateException("Записываемый пользователь должен иметь роль STUDENT (или ADMIN для теста).");
        }
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Курс не найден: id=" + courseId));

        if (enrollmentRepository.existsByStudent_IdAndCourse_Id(studentId, courseId)) {
            throw new IllegalStateException("Студент уже записан на этот курс.");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setStatus(Enrollment.Status.ACTIVE);
        return enrollmentRepository.save(enrollment);
    }

    @Transactional
    public void unenrollStudent(Long courseId, Long studentId, Long actorUserId) {
        User actor = userRepository.findById(actorUserId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден: id=" + actorUserId));
        if (actor.getRole() == Role.STUDENT && !actor.getId().equals(studentId)) {
            throw new IllegalStateException("Студент может отписывать только себя.");
        }
        List<Enrollment> list = enrollmentRepository.findByStudent_Id(studentId)
                .stream().filter(e -> e.getCourse().getId().equals(courseId)).toList();
        if (list.isEmpty()) {
            throw new EntityNotFoundException("Запись на курс не найдена.");
        }
        enrollmentRepository.deleteAll(list);
    }
}
