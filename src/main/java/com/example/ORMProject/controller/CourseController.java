package com.example.ORMProject.controller;

import com.example.ORMProject.model.Course;
import com.example.ORMProject.service.CourseService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Контроллер управления курсами. Бизнес-правила (кто может создавать/удалять и т.п.) реализованы в CourseService.

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // Список всех курсов.

    @GetMapping
    public List<Course> findAll() {
        return courseService.findAll();
    }

    // Получение курса по ID.

    @GetMapping("/{id}")
    public Course findById(@PathVariable Long id) {
        return courseService.findById(id);
    }

    // Создание курса. Пример: POST /api/courses?title=Hibernate&description=...&teacherId=3&categoryId=2&durationWeeks=6

    @PostMapping
    public Course createCourse(@RequestParam String title,
                               @RequestParam(required = false) String description,
                               @RequestParam Long teacherId,
                               @RequestParam(required = false) Long categoryId,
                               @RequestParam(required = false) Integer durationWeeks) {
        return courseService.createCourse(title, description, teacherId, categoryId, durationWeeks);
    }


     // Обновление курса. Пример: PUT /api/courses/10?newTitle=...&newTeacherId=5

    @PutMapping("/{courseId}")
    public Course updateCourse(@PathVariable Long courseId,
                               @RequestParam(required = false) String newTitle,
                               @RequestParam(required = false) String newDescription,
                               @RequestParam(required = false) Long newTeacherId,
                               @RequestParam(required = false) Long newCategoryId,
                               @RequestParam(required = false) Integer newDurationWeeks) {
        return courseService.updateCourse(courseId, newTitle, newDescription, newTeacherId, newCategoryId, newDurationWeeks);
    }

    //     Удаление курса (только ADMIN).   Пример: DELETE /api/courses/10?actorUserId=1

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId,
                                             @RequestParam Long actorUserId) {
        courseService.deleteCourse(courseId, actorUserId);
        return ResponseEntity.noContent().build();
    }


   // Пример обработки ошибок.

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
