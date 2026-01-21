package com.jewelry.workshop.presentation.controller;

import com.jewelry.workshop.domain.model.dto.auth.*;
import com.jewelry.workshop.presentation.exception.error.ErrorResponseDTO;
import com.jewelry.workshop.service.interfaces.AuthService;
import com.jewelry.workshop.util.RateLimit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Аутентификация", description = "Регистрация, вход, обновление токенов")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Регистрация нового клиента")
    @ApiResponse(responseCode = "200", description = "Успешная регистрация",
            content = @Content(schema = @Schema(implementation = AuthResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса (валидация)",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "409", description = "Email уже зарегистрирован",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        AuthResponseDTO response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @RateLimit(maxAttempts = 5, windowMinutes = 5, keyPrefix = "login")
    @Operation(summary = "Вход в систему")
    @ApiResponse(responseCode = "200", description = "Успешный вход",
            content = @Content(schema = @Schema(implementation = AuthResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Email не подтверждён",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "401", description = "Неверный email или пароль",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify-email")
    @RateLimit(maxAttempts = 5, windowMinutes = 10, keyPrefix = "verify_email")
    @Operation(summary = "Подтверждение email по токену")
    @ApiResponse(responseCode = "200", description = "Email успешно подтверждён")
    @ApiResponse(responseCode = "429", description = "Слишком много запросов. Попробуйте позже.")
    @ApiResponse(responseCode = "400", description = "Неверный или истёкший токен",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok("Email успешно подтверждён. Теперь вы можете войти.");
    }

    @PostMapping("/forgot-password")
    @RateLimit(maxAttempts = 3, windowMinutes = 60, keyPrefix = "forgot_password")
    @Operation(summary = "Запросить сброс пароля")
    @ApiResponse(responseCode = "200", description = "Ссылка отправлена")
    @ApiResponse(responseCode = "404", description = "Пользователь с таким email не найден",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody PasswordResetRequestDTO request) {
        authService.initiatePasswordReset(request.getEmail());
        return ResponseEntity.ok("Ссылка для сброса пароля отправлена на ваш email.");
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Сбросить пароль по токену")
    @ApiResponse(responseCode = "200", description = "Пароль успешно изменён")
    @ApiResponse(responseCode = "429", description = "Слишком много запросов. Попробуйте позже.")
    @ApiResponse(responseCode = "400", description = "Пароли не совпадают или токен недействителен",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public ResponseEntity<String> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword) {

        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("Пароли не совпадают.");
        }
        authService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Пароль успешно изменён.");
    }

    @GetMapping("/reset-password-form")
    @Operation(summary = "Форма сброса пароля (для frontend)")
    @ApiResponse(responseCode = "200", description = "HTML-форма для сброса пароля")
    public ResponseEntity<String> showResetForm(@RequestParam String token) {
        return ResponseEntity.ok("<html><body>" +
                "<h2>Введите новый пароль</h2>" +
                "<form action=\"/api/auth/reset-password\" method=\"post\">" +
                "<input type=\"hidden\" name=\"token\" value=\"" + token + "\" />" +
                "<input type=\"password\" name=\"newPassword\" placeholder=\"Новый пароль\" required />" +
                "<input type=\"password\" name=\"confirmPassword\" placeholder=\"Подтвердите пароль\" required />" +
                "<button type=\"submit\">Сбросить пароль</button>" +
                "</form>" +
                "</body></html>");
    }

    @PostMapping("/logout")
    @Operation(summary = "Выход из системы")
    @ApiResponse(responseCode = "200", description = "Успешный выход")
    @ApiResponse(responseCode = "400", description = "Неверный refresh токен",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public ResponseEntity<String> logout(@Valid @RequestBody LogoutRequestDTO request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok("Вы успешно вышли из системы");
    }

    @PostMapping("/refresh")
    @Operation(summary = "Обновить access-токен с помощью refresh-токена")
    @ApiResponse(responseCode = "200", description = "Новые токены успешно выданы",
            content = @Content(schema = @Schema(implementation = AuthResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Неверный или просроченный refresh-токен",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public ResponseEntity<AuthResponseDTO> refreshToken(@RequestBody RefreshTokenRequestDTO request) {
        AuthResponseDTO response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Сбросить пароль пользователя (только ADMIN)")
    @ApiResponse(responseCode = "200", description = "Пароль успешно изменён")
    @ApiResponse(responseCode = "403", description = "Доступ запрещён",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Пользователь не найден",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public ResponseEntity<String> resetUserPassword(
            @PathVariable Long id,
            @Valid @RequestBody AdminPasswordResetDTO dto
    ) {
        authService.resetUserPassword(id, dto.getNewPassword());
        return ResponseEntity.ok("Пароль пользователя #" + id + " успешно сброшен");
    }
}