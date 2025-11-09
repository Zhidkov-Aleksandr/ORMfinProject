package com.example.ORMProject.it;

import com.example.ORMProject.model.Course;
import com.example.ORMProject.model.Module;
import com.example.ORMProject.repository.CourseRepository;
import com.example.ORMProject.repository.ModuleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

// Проверяем поведение ленивой загрузки.  В этом тесте ОСИВ выключен: spring.jpa.open-in-view=false

@TestPropertySource(properties = {
        "spring.jpa.open-in-view=false"
})
public class LazyLoadingIntegrationTest extends BasePostgresIT {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    void testLazyInitializationException() {
        // Подготовка данных
        Course course = new Course();
        course.setTitle("Hibernate Lazy Loading");
        Module m = new Module();
        m.setTitle("Lazy vs Eager");
        m.setCourse(course);
        course.getModules().add(m);
        courseRepository.save(course);

        Long courseId = course.getId();
        Assertions.assertNotNull(courseId);

        // Вынесем за транзакцию: просто читаем курс по id (коллекция modules = LAZY)
        Course found = courseRepository.findById(courseId).orElseThrow();

        // Попытка доступа к ленивой коллекции после выхда из репозитория
        Assertions.assertThrows(LazyInitializationException.class, () -> {
            found.getModules().size(); // нет открытой сессии => LazyInitializationException
        });
    }

    @Test
    void testJoinFetchAvoidsLazyInitException() {
        // Подготовка
        Course c = new Course();
        c.setTitle("JPA Fetch Join");
        Module m1 = new Module();
        m1.setTitle("JOIN FETCH 1");
        m1.setCourse(c);
        c.getModules().add(m1);
        courseRepository.save(c);
        Long id = c.getId();

        // Решение: заберём курс вместе с модулями через JPQL JOIN FETCH — внутри теста через EntityManager
        Course withModules = em.createQuery("""
                SELECT c FROM Course c
                LEFT JOIN FETCH c.modules
                WHERE c.id = :id
                """, Course.class)
                .setParameter("id", id)
                .getSingleResult();

        // Теперь коллекция инициаллизирована и доступна
        Assertions.assertEquals(1, withModules.getModules().size());
    }
}
