package com.jewelry.workshop.domain.model.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO для подтверждения сброса пароля")
public class PasswordResetConfirmDTO {

    @NotBlank(message = "Токен обязателен")
    @Schema(description = "Токен сброса пароля", required = true)
    private String token;

    @NotBlank(message = "Новый пароль обязателен")
    @Size(min = 6, message = "Новый пароль должен содержать минимум 6 символов")
    @Schema(description = "Новый пароль", required = true)
    private String newPassword;

    @NotBlank(message = "Подтверждение пароля обязательно")
    @Schema(description = "Подтверждение нового пароля", required = true)
    private String confirmPassword;
}