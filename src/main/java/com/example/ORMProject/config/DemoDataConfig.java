package com.example.ORMProject.config;

import com.example.ORMProject.model.Course;
import com.example.ORMProject.model.Module;
import com.example.ORMProject.model.Role;
import com.example.ORMProject.model.User;
import com.example.ORMProject.repository.CourseRepository;
import com.example.ORMProject.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;


 // Простой сидер для демо. Создаёт трёх пользователей и один курс с модулем.
 // Логин — email, пароль — как указан в коде (захэширован).

@Configuration
public class DemoDataConfig {

    @Bean
    public CommandLineRunner demoData(UserRepository userRepository,
                                      CourseRepository courseRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            // Пользователи
            if (userRepository.count() == 0) {
                User admin = new User();
                admin.setName("Админ");
                admin.setEmail("admin@example.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);

                User teacher = new User();
                teacher.setName("Преподаватель");
                teacher.setEmail("teacher@example.com");
                teacher.setPassword(passwordEncoder.encode("teach123"));
                teacher.setRole(Role.TEACHER);
                userRepository.save(teacher);

                User student = new User();
                student.setName("Студент");
                student.setEmail("student@example.com");
                student.setPassword(passwordEncoder.encode("stud123"));
                student.setRole(Role.STUDENT);
                userRepository.save(student);
            }

            // Курс + модуль
            if (courseRepository.count() == 0) {
                User teacher = userRepository.findByEmail("teacher@example.com")
                        .orElseThrow(() -> new IllegalStateException("Teacher user not found"));

                Course c = new Course();
                c.setTitle("Демо курс: Hibernate");
                c.setDescription("Краткий курс для демонстрации UI и ленивой загрузки");
                c.setTeacher(teacher);

                Module m = new Module();
                m.setTitle("Введение в ORM");
                m.setCourse(c);

                // благодаря orphanRemoval/cascade модуль сохранится вместе с курсом
                c.getModules().add(m);

                courseRepository.save(c);
            }
        };
    }
}
