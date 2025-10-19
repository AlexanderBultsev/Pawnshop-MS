package com.pawnshop.authorizationserver.config;

import com.pawnshop.authorizationserver.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Хэширование паролей
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return username -> userRepository.findByUsername(username)
                .map(user -> User.withUsername(user.getUsername())
                        .password(user.getPassword())
                        .roles(user.getRoles().split(","))
                        .build());
    }

}
