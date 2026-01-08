package com.jewelry.workshop.presentation.controller;

import com.jewelry.workshop.domain.model.dto.auth.AuthResponseDTO;
import com.jewelry.workshop.domain.model.dto.auth.LoginRequestDTO;
import com.jewelry.workshop.domain.model.dto.auth.RegisterRequestDTO;
import com.jewelry.workshop.servise.interfaces.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
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
}
