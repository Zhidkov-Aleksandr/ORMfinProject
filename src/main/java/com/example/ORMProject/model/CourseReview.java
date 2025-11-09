package com.example.ORMProject.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Курс, который оценивается
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // Студент, оставивший отзыв
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(nullable = false)
    private int rating;            // оценка курса (например, 1-5)

    private String comment;        // текст отзыва

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();  // время создания отзыва
}
