package com.example.ORMProject.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_submissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Пройденный тест
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    // Студент, который проходил тест
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    private Integer score;          // набранный балл (число правильных ответов или процент)
    private LocalDateTime takenAt = LocalDateTime.now();  // время прохождения теста
}
