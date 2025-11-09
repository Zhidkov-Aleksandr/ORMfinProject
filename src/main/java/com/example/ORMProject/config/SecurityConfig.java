package com.example.ORMProject.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // отключаем CSRF для простоты
                .authorizeHttpRequests(auth -> auth
                        // Доступ к swagger и api-docs без авторизации
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // Остальные запросы требуют авторизации
                        .anyRequest().authenticated()
                )
                // Используем базовую аутентификацию (логин/пароль через HTTP Basic)
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
