package com.jewelry.workshop.servise.impl;

import com.jewelry.workshop.domain.model.dto.auth.AuthResponseDTO;
import com.jewelry.workshop.domain.model.dto.auth.AuthUserDTO;
import com.jewelry.workshop.domain.model.dto.auth.LoginRequestDTO;
import com.jewelry.workshop.domain.model.dto.auth.RegisterRequestDTO;
import com.jewelry.workshop.domain.model.entity.Client;
import com.jewelry.workshop.domain.model.entity.User;
import com.jewelry.workshop.domain.repository.ClientRepository;
import com.jewelry.workshop.domain.repository.UserRepository;
import com.jewelry.workshop.infrastructure.email.EmailService; // ← ИСПРАВЛЕНО
import com.jewelry.workshop.security.auth.UserDetailsImpl;
import com.jewelry.workshop.security.jwt.JwtTokenProvider;
import com.jewelry.workshop.servise.interfaces.AuthService;
import com.jewelry.workshop.util.Constants;
import com.jewelry.workshop.util.PasswordUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final PasswordUtil passwordUtil;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Override
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email уже зарегистрирован");
        }
        if(!request.getPassword().equals(request.getConfirmPassword())){
            throw new RuntimeException("Пароли не совпадают");
        }

        String username = generateUniqueUsername(request.getFirstName(), request.getLastName());

        User user = new User();
        user.setUsername(username);
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordUtil.encode(request.getPassword()));
        user.setRole(User.Role.valueOf(Constants.ROLE_CLIENT));
        user.setEnabled(false); // ← Не активен
        user.setEmailVerified(false); // ← Email не подтверждён
        user.setVerificationToken(UUID.randomUUID().toString()); // ← Токен
        user.setVerificationTokenExpiresAt(Instant.now().plus(24, ChronoUnit.HOURS)); // ← Срок действия
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        User savedUser = userRepository.save(user);

        Client client = new Client();
        client.setFirstName(request.getFirstName());
        client.setLastName(request.getLastName());
        client.setPatronymic(request.getPatronymic());
        client.setPhone(request.getPhone());
        client.setUser(savedUser);
        client.setCreatedAt(Instant.now());
        client.setUpdatedAt(Instant.now());

        clientRepository.save(client);

        emailService.sendVerificationEmail(user.getEmail(), user.getVerificationToken());

        return AuthResponseDTO.builder()
                .message("Регистрация успешна. Проверьте ваш email для подтверждения.")
                .build();
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getEmail()).orElseThrow();

        // Проверяем, подтверждён ли email
        if (!user.isEmailVerified()) {
            throw new RuntimeException("Email не подтверждён. Проверьте вашу почту.");
        }

        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        String fullName = user.getUsername();
        if(user.getRole() == User.Role.CLIENT){
            Client client = clientRepository.findByUserId(user.getId()).orElseThrow();
            fullName = client.getFullName();
        }

        AuthUserDTO authUser = new AuthUserDTO();
        authUser.setId(user.getId());
        authUser.setEmail(user.getEmail());
        authUser.setRole(user.getRole().name());
        authUser.setFullName(fullName);

        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(Constants.ACCESS_TOKEN_EXPIRE/1000)
                .user(authUser)
                .build();
    }

    @Override
    @Transactional
    public boolean verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Неверный токен"));

        if (user.getVerificationTokenExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Токен истёк");
        }

        user.setEmailVerified(true);
        user.setEnabled(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiresAt(null);
        userRepository.save(user);

        emailService.sendWelcomeEmail(user.getEmail());

        return true;
    }

    private String generateUniqueUsername(String firstName, String lastName){
        String base = (firstName + "." + lastName).toLowerCase().replace("[^a-z0-9.]", "");
        String username = base;
        int counter = 1;
        while(userRepository.existsByUsername(username)){
            username = base + counter++;
        }
        return username;
    }

    @Override
    public void initiatePasswordReset(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь с таким email не найден"));

        String token = UUID.randomUUID().toString();
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS));

        userRepository.save(user);

        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    @Override
    @Transactional
    public boolean resetPassword(String token, String newPassword){
        User user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new RuntimeException("Неверный или истёкший токен"));

        if(user.getPasswordResetTokenExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Токен истёк");
        }

        user.setPasswordHash(passwordUtil.encode(newPassword));


        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiresAt(null);

        userRepository.save(user);

        return true;
    }

    @Override
    @Transactional
    public void logout(String refreshToken){
        if(!jwtTokenProvider.validateToken(refreshToken)){
            throw new RuntimeException("Неверный refresh токен");
        }

        String username = jwtTokenProvider.getUsernameFromJwt(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        user.setRefreshTokenHash(null);
        userRepository.save(user);
    }
}