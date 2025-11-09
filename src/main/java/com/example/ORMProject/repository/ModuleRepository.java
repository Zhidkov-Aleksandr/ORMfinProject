package com.example.ORMProject.repository;

import com.example.ORMProject.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ModuleRepository extends JpaRepository<Module, Long> {
    List<Module> findByCourse_Id(Long courseId);
}
