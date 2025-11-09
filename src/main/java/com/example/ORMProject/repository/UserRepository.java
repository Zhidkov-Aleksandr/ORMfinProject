package com.example.ORMProject.repository;

import com.example.ORMProject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Дополнительно: метод поиска по email (для входа в систему, например)
    Optional<User> findByEmail(String email);

}
