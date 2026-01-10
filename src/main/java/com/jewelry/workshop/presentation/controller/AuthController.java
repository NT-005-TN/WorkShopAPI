package com.jewelry.workshop.presentation.controller;

import com.jewelry.workshop.domain.model.dto.auth.*;
import com.jewelry.workshop.servise.interfaces.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name =  "Аутентификация", description = "Регистрация, вход, обновление токенов")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Регистрация нового клиента")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        AuthResponseDTO response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Вход в систему")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request){
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify-email")
    @Operation(summary = "Подтверждение email по токену")
    public ResponseEntity<String> verifyEmail(@RequestParam String token){
        boolean success = authService.verifyEmail(token);
        if(success) {
            return ResponseEntity.ok("Email успешно подтверждён. Теперь вы можете войти.");
        } else {
            return ResponseEntity.badRequest().body("Не удалось подтвердить email.");
        }
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Запросить сброс пароля")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody PasswordResetRequestDTO request){
        authService.initiatePasswordReset(request.getEmail());
        return ResponseEntity.ok("Ссылка для сброса пароля отправлена на ваш email.");
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Сбросить пароль по токену")
    public ResponseEntity<String> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword) {

        if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body("Пароли не совпадают.");
        }

        boolean success = authService.resetPassword(token, newPassword);
        if (success) {
            return ResponseEntity.ok("Пароль успешно изменён.");
        } else {
            return ResponseEntity.badRequest().body("Не удалось сбросить пароль.");
        }
    }

    // GET эндпоинт для формы сброса (frontend)
    @GetMapping("/reset-password-form")
    @Operation(summary = "Форма сброса пароля (для frontend)")
    public ResponseEntity<String> showResetForm(@RequestParam String token) {
        // В идеале, это должен быть HTML-шаблон, но можно просто вернуть сообщение
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
    public ResponseEntity logout(@Valid @RequestBody LogoutRequestDTO request){
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok("Вы успешно вышли из системы");
    }
}
