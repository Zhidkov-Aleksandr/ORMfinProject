package com.example.ORMProject.repository;

import com.example.ORMProject.model.CourseReview;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {
    List<CourseReview> findByCourse_Id(Long courseId);
    List<CourseReview> findByStudent_Id(Long studentId);
}
