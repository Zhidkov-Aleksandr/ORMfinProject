package com.example.ORMProject.repository;

import com.example.ORMProject.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByAssignment_Id(Long assignmentId);
    List<Submission> findByStudent_Id(Long studentId);
    Optional<Submission> findByStudent_IdAndAssignment_Id(Long studentId, Long assignmentId);
}
