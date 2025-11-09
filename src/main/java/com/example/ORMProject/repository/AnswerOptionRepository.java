package com.example.ORMProject.repository;

import com.example.ORMProject.model.AnswerOption;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnswerOptionRepository extends JpaRepository<AnswerOption, Long> {
    List<AnswerOption> findByQuestion_Id(Long questionId);
}
