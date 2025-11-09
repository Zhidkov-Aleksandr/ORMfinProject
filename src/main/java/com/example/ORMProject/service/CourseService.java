package com.example.ORMProject.service;

import com.example.ORMProject.model.Category;
import com.example.ORMProject.model.Course;
import com.example.ORMProject.model.User;
import com.example.ORMProject.model.Role;
import com.example.ORMProject.repository.CategoryRepository;
import com.example.ORMProject.repository.CourseRepository;
import com.example.ORMProject.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public CourseService(CourseRepository courseRepository,
                         UserRepository userRepository,
                         CategoryRepository categoryRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Course findById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Курс не найден: id=" + id));
    }

    @Transactional
    public Course createCourse(String title,
                               String description,
                               Long teacherId,
                               Long categoryId,
                               Integer durationWeeks) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь-преподаватель не найден: id=" + teacherId));
        if (teacher.getRole() != Role.TEACHER && teacher.getRole() != Role.ADMIN) {
            throw new IllegalStateException("Курс может создавать только преподаватель или администратор.");
        }
        Category category = null;
        if (categoryId != null) {
            category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new EntityNotFoundException("Категория не найдена: id=" + categoryId));
        }
        Course course = new Course();
        course.setTitle(title);
        course.setDescription(description);
        course.setTeacher(teacher);
        course.setCategory(category);
        course.setDurationWeeks(durationWeeks);
        return courseRepository.save(course);
    }

    @Transactional
    public Course updateCourse(Long courseId,
                               String newTitle,
                               String newDescription,
                               Long newTeacherId,
                               Long newCategoryId,
                               Integer newDurationWeeks) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Курс не найден: id=" + courseId));

        if (newTitle != null) course.setTitle(newTitle);
        if (newDescription != null) course.setDescription(newDescription);
        if (newTeacherId != null) {
            User teacher = userRepository.findById(newTeacherId)
                    .orElseThrow(() -> new EntityNotFoundException("Пользователь-преподаватель не найден: id=" + newTeacherId));
            if (teacher.getRole() != Role.TEACHER && teacher.getRole() != Role.ADMIN) {
                throw new IllegalStateException("Преподавателем курса может быть только пользователь с ролью TEACHER/ADMIN.");
            }
            course.setTeacher(teacher);
        }
        if (newCategoryId != null) {
            Category category = categoryRepository.findById(newCategoryId)
                    .orElseThrow(() -> new EntityNotFoundException("Категория не найдена: id=" + newCategoryId));
            course.setCategory(category);
        }
        if (newDurationWeeks != null) {
            course.setDurationWeeks(newDurationWeeks);
        }
        return courseRepository.save(course);
    }

    @Transactional
    public void deleteCourse(Long courseId, Long actorUserId) {
        User actor = userRepository.findById(actorUserId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден: id=" + actorUserId));
        if (actor.getRole() != Role.ADMIN) {
            throw new IllegalStateException("Удалять курсы может только администратор.");
        }
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Курс не найден: id=" + courseId));
        courseRepository.delete(course);
    }
}
