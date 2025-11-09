package com.example.ORMProject.web;

import com.example.ORMProject.model.Course;
import com.example.ORMProject.model.Module;
import com.example.ORMProject.service.CourseService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Простой MVC-контроллер для страниц на Thymeleaf.
// Список курсов
// Детали курса (модули)
// Форма создания курса
// Обрати внимание: чтобы избежать LazyInitializationException на странице деталей,
// есть метод с явной подгрузкой модулей через JOIN FETCH.

@Controller
@RequestMapping("/ui")
public class UiController {

    private final CourseService courseService;

    @PersistenceContext
    private EntityManager em;

    public UiController(CourseService courseService) {
        this.courseService = courseService;
    }

    // Главная страница UI -> редирект на список курсов.

    @GetMapping
    public String home() {
        return "redirect:/ui/courses";
    }

    // Список курсов (без модулей). Шаблон сам список отобразит, а ссылка "Подробнее" ведёт на страницу с модулями.

    @GetMapping("/courses")
    public String courses(Model model) {
        List<Course> all = courseService.findAll();
        model.addAttribute("courses", all);
        model.addAttribute("createForm", new CreateCourseForm());
        return "courses";
    }


     // Детали курса + модули.
     // Вариант 1 (безопасный): грузим курс c модулями через JOIN FETCH внутри @Transactional.
     // Это гарантирует, что modules инициаллизированы к моменту рендеринга.

    @GetMapping("/courses/{id}")
    @Transactional(readOnly = true)
    public String courseDetails(@PathVariable Long id, Model model) {
        // JOIN FETCH — гарантируем подгрузку модулей
        Course c = em.createQuery("""
                SELECT c FROM Course c
                LEFT JOIN FETCH c.modules
                WHERE c.id = :id
                """, Course.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Курс не найден: id=" + id));

        model.addAttribute("course", c);
        model.addAttribute("modules", c.getModules());
        return "course-details";
    }


     // Создание курса через форму. Разрешим только TEACHER/ADMIN (пример).
     // Если включён метод-level security, аннотация будет работать.

    @PostMapping("/courses")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public String createCourse(@ModelAttribute("createForm") CreateCourseForm form) {
        courseService.createCourse(
                form.getTitle(),
                form.getDescription(),
                form.getTeacherId(),
                form.getCategoryId(),
                form.getDurationWeeks()
        );
        return "redirect:/ui/courses";
    }

    // Простейший DTO для формы создания курса.

    public static class CreateCourseForm {
        private String title;
        private String description;
        private Long teacherId;
        private Long categoryId;
        private Integer durationWeeks;

        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }
        public String getDescription() {
            return description;
        }
        public void setDescription(String description) {
            this.description = description;
        }
        public Long getTeacherId() {
            return teacherId;
        }
        public void setTeacherId(Long teacherId) {
            this.teacherId = teacherId;
        }
        public Long getCategoryId() {
            return categoryId;
        }
        public void setCategoryId(Long categoryId) {
            this.categoryId = categoryId;
        }
        public Integer getDurationWeeks() {
            return durationWeeks;
        }
        public void setDurationWeeks(Integer durationWeeks) {
            this.durationWeeks = durationWeeks;
        }
    }
}
