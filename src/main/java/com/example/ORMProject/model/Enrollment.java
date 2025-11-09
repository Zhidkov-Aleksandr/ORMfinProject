package com.example.ORMProject.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "enrollments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Студент, который записан на курс
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    // Курс, на который записан студент
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private LocalDate enrollDate = LocalDate.now();  // дата записи (по умолчанию текущая)

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;  // статус записи (ACTIVE, COMPLETED, etc.)

    // Перечисление статусов записи
    public enum Status {
        ACTIVE,
        COMPLETED,
        CANCELED
    }
}
