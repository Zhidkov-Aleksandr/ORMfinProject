package com.example.ORMProject.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    // BCrypt — хранить хэши паролей.

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager на базе нашего UserDetailsService.

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    // Правила доступа.

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                // Правила авторизации
                .authorizeHttpRequests(reg -> reg
                        // Swagger и OpenAPI — открыть
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Главная/статические/минимальный UI — открыть (если используешь Thymeleaf)
                        .requestMatchers("/", "/css/**", "/js/**").permitAll()

                        // Чтение курсов/модулей/пользователей — разрешим всем (для демо)
                        .requestMatchers(HttpMethod.GET, "/api/**").permitAll()

                        // Любые изменения курсов/модулей/заданий/квизов — TEACHER или ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/courses/**", "/api/modules/**", "/api/assignments/**", "/api/quizzes/**").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/courses/**", "/api/modules/**", "/api/assignments/**", "/api/quizzes/**").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/courses/**", "/api/modules/**", "/api/assignments/**", "/api/quizzes/**").hasAnyRole("TEACHER", "ADMIN")

                        // Прохождение теста и отправка решений — STUDENT или ADMIN
                        .requestMatchers("/api/submissions/**", "/api/quiz-submissions/**").hasAnyRole("STUDENT", "ADMIN")

                        // Остальное — требовать аутентификацию
                        .anyRequest().authenticated()
                )

                // HTTP Basic для простоты
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

}
