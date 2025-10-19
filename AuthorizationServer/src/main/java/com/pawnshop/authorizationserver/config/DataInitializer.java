package com.pawnshop.authorizationserver.config;

import com.pawnshop.authorizationserver.entity.OAuth2Client;
import com.pawnshop.authorizationserver.entity.User;
import com.pawnshop.authorizationserver.repository.OAuth2ClientRepository;
import com.pawnshop.authorizationserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final OAuth2ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Инициализация тестового пользователя
        User user = new User();
        user.setUsername("admin");
        user.setPassword(passwordEncoder.encode("admin"));
        user.setRoles("USER,ADMIN");

        userRepository.save(user).subscribe();

        // Инициализация клиента OAuth2
        OAuth2Client client = new OAuth2Client();
        client.setClientId("pawnshop-client");
        client.setClientSecret(passwordEncoder.encode("client-secret"));
        client.setRedirectUris("http://localhost:8080/login/oauth2/code/pawnshop-client");
        client.setScopes("openid,profile,read,write");
        client.setGrantTypes("authorization_code,password,refresh_token");

        clientRepository.save(client).subscribe();
    }
}
