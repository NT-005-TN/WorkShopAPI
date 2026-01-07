package com.jewelry.workshop.domain.model.dto.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "DTO для создания клиента с паролем (только для админов/продавцов)")
public class ClientCreateWithPasswordDTO {

    @NotBlank(message = "Имя обязательно")
    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    @Schema(description = "Имя клиента", example = "Иван", required = true)
    private String firstName;

    @NotBlank(message = "Фамилия обязательна")
    @Size(min = 2, max = 50, message = "Фамилия должна быть от 2 до 50 символов")
    @Schema(description = "Фамилия клиента", example = "Петров", required = true)
    private String lastName;

    @Size(max = 50, message = "Отчество не должно превышать 50 символов")
    @Schema(description = "Отчество клиента", example = "Сергеевич")
    private String patronymic;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    @Schema(description = "Email клиента", example = "ivan.petrov@example.com", required = true)
    private String email;

    @NotBlank(message = "Телефон обязателен")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Некорректный формат номера телефона")
    @Schema(description = "Телефон клиента", example = "+79991234567", required = true)
    private String phone;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, message = "Пароль должен содержать минимум 6 символов")
    @Schema(description = "Пароль для аккаунта клиента", example = "password123", required = true)
    private String password;

    @Schema(description = "Является ли клиент постоянным", example = "false")
    private Boolean isPermanent = false;
}