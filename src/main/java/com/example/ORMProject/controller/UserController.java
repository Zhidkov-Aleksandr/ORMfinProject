package com.example.ORMProject.controller;

import com.example.ORMProject.model.Role;
import com.example.ORMProject.model.User;
import com.example.ORMProject.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Простой контроллер для пользователей (без паролей/шифрования, только для демо).

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Все пользователи.

    @GetMapping
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // Создать пользователя. Пример: POST /api/users?name=Ivan&email=ivan@ex.com&password=123&role=TEACHER

    @PostMapping
    public User create(@RequestParam String name,
                       @RequestParam String email,
                       @RequestParam String password,
                       @RequestParam Role role) {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPassword(password);
        u.setRole(role);
        return userRepository.save(u);
    }

    // Получить по id.

    @GetMapping("/{id}")
    public User byId(@PathVariable Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден: id=" + id));
    }

    // Удалить по id.

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("Пользователь не найден: id=" + id);
        }
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
