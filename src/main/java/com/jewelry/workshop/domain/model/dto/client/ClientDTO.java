package com.jewelry.workshop.domain.model.dto.client;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "DTO для клиента")
public class ClientDTO {

    @Schema(description = "ID клиента", example = "1")
    private Long id;

    @NotBlank(message = "Имя обязательно для заполнения")
    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    @Schema(description = "Имя клиента", example = "Иван", required = true)
    private String firstName;

    @NotBlank(message = "Фамилия обязательна для заполнения")
    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    @Schema(description = "Фамилия клиента", example = "Петров", required = true)
    private String lastName;

    @Size(max = 50, message = "Отчество не должно превышать 50 символов")
    @Schema(description = "Отчество клиента", example = "Сергеевич")
    private String patronymic;

    @NotBlank(message = "Email обязателен для заполнения")
    @Email(message = "Некорректный формат email")
    @Size(max = 100, message = "Email не должен превышать 100 символов")
    @Schema(description = "Email клиента", example = "ivan.petrov@example.com", required = true)
    private String email;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Некорректный формат номера телефона")
    @Schema(description = "Телефон клиента", example = "+79991234567")
    private String phone;

    @Schema(description = "Является ли клиент постоянным", example = "false")
    private Boolean isPermanent = false;

    @Schema(description = "Полное имя клиента", example = "Петров Иван Сергеевич")
    private String fullName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Дата создания", example = "2024-01-15 14:30:00")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Дата обновления", example = "2024-01-15 14:30:00")
    private LocalDateTime updatedAt;

}
