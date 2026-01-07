package com.jewelry.workshop.domain.model.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO для запроса регистрации нового клиента")
public class RegisterRequestDTO {

    @NotBlank(message = "Имя обязательно")
    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    @Schema(description = "Имя пользователя", example = "Иван", required = true)
    private String firstName;

    @NotBlank(message = "Фамилия обязательна")
    @Size(min = 2, max = 50, message = "Фамилия должна быть от 2 до 50 символов")
    @Schema(description = "Фамилия пользователя", example = "Петров", required = true)
    private String lastName;

    @Size(max = 50, message = "Отчество не должно превышать 50 символов")
    @Schema(description = "Отчество", example = "Сергеевич")
    private String patronymic;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    @Schema(description = "Email пользователя", example = "ivan.petrov@example.com", required = true)
    private String email;

    @NotBlank(message = "Телефон обязателен")
    @Size(min = 10, max = 15, message = "Телефон должен быть от 10 до 15 символов")
    @Schema(description = "Номер телефона", example = "+79991234567", required = true)
    private String phone;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, message = "Пароль должен содержать минимум 6 символов")
    @Schema(description = "Пароль", example = "password123", required = true)
    private String password;

    @NotBlank(message = "Подтверждение пароля обязательно")
    @Schema(description = "Подтверждение пароля", example = "password123", required = true)
    private String confirmPassword;
}