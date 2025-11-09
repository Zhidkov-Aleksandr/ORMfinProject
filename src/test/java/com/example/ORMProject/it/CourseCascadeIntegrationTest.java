package com.example.ORMProject.it;


import com.example.ORMProject.model.Course;
import com.example.ORMProject.model.Module;
import com.example.ORMProject.repository.CourseRepository;
import com.example.ORMProject.repository.ModuleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

// Проверяем:
// каскадное сохранение модулей при сохранении курса;
// orphanRemoval/cascade при удалении курса;
// базовый CRUD по курсу и модулям.


public class CourseCascadeIntegrationTest extends BasePostgresIT {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    @Transactional
    void testCreateCourseWithModules_andDeleteCascade() {
        // 1) Создаём курс с 2 модулями
        Course c = new Course();
        c.setTitle("Основы Hibernate");
        c.setDescription("Базовый курс по Hibernate/JPA");
        Module m1 = new Module();
        m1.setTitle("Введение");
        Module m2 = new Module();
        m2.setTitle("Связи и каскады");

        // двунаправленные связи
        m1.setCourse(c);
        m2.setCourse(c);
        c.getModules().addAll(List.of(m1, m2));

        // Сохраняем курпс, ожидая каскадный persist модулей
        Course saved = courseRepository.save(c);
        Long courseId = saved.getId();
        Assertions.assertNotNull(courseId, "ID курса должен быть присвоен");

        // 2) Сбрасываем и очищаем контекст, чтобы читать заново из БД
        em.flush();
        em.clear();

        Course fromDb = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalStateException("Курс не найден после сохранения"));
        Assertions.assertEquals("Основы Hibernate", fromDb.getTitle());

        // Проверим, что модули действительно сохранились
        List<Module> modules = moduleRepository.findByCourse_Id(courseId);
        Assertions.assertEquals(2, modules.size(), "Ожидалось 2 модуля у курса");

        // 3) Удалим курс — ожидаем удаление связанных модулей (если у связи orphanRemoval=true)
        courseRepository.delete(fromDb);
        em.flush();

        // 4) Проверим, что ни курса, ни модулей не осталось
        Assertions.assertTrue(courseRepository.findById(courseId).isEmpty(), "Курс должен быть удалён");
        Assertions.assertEquals(0L, moduleRepository.count(), "Модули курса должны быть удалены каскадно/orphanRemoval");
    }
}
