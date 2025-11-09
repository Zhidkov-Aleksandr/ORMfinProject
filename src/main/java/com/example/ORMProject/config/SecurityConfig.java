package com.example.ORMProject.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(reg -> reg
                        // Swagger и OpenAPI — открыть
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Главная/статические — открыть
                        .requestMatchers("/", "/css/**", "/js/**").permitAll()

                        // GET к API — открыть для демо
                        .requestMatchers(HttpMethod.GET, "/api/**").permitAll()

                        // CRUD по учебке — TEACHER/ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/courses/**", "/api/modules/**", "/api/assignments/**", "/api/quizzes/**").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/courses/**", "/api/modules/**", "/api/assignments/**", "/api/quizzes/**").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/courses/**", "/api/modules/**", "/api/assignments/**", "/api/quizzes/**").hasAnyRole("TEACHER", "ADMIN")

                        // Отправка решений/прохождение тестов — STUDENT/ADMIN
                        .requestMatchers("/api/submissions/**", "/api/quiz-submissions/**").hasAnyRole("STUDENT", "ADMIN")

                        // Остальное — требовать аутентификацию
                        .anyRequest().authenticated()
                )

                // Basic auth для простоты
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
