package com.example.ORMProject.security;

import com.example.ORMProject.model.User;
import com.example.ORMProject.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

// Вспомогательный компонент, чтобы получать текущего User из БД по email,
// который хранится как username в SecurityContext.

@Component
public class AuthFacade {

    private final UserRepository userRepository;

    public AuthFacade(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Текущий аутентифицированный User (из БД).

    public User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new EntityNotFoundException("Пользователь не аутентифицирован.");
        }
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден по email: " + email));
    }

    //  Удобный доступ к id.

    public Long currentUserId() {
        return currentUser().getId();
    }
}
