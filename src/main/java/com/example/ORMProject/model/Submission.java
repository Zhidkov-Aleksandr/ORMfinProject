package com.example.ORMProject.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Задание, которое выполняется
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    // Студент, отправивший решение
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(columnDefinition = "TEXT")
    private String content;        // содержимое решения (текст ответа или путь к файлу)

    private LocalDateTime submittedAt = LocalDateTime.now();  // время отправки

    private Integer score;         // оценка преподавателя (после проверки)
    private String feedback;       // отзыв/комментарий преподавателя к работе
}
