package com.jewelry.workshop.domain.model.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO для регистрации клиента")
public class AuthRequestDTO {
    @Schema(description = "ID клиента", example = "1")
    private Long id;

    @NotBlank(message = "Имя обязательно для заполнения")
    @Size(min = 2, max = 50, message = "Имя должно быть от {min} до {max} символов")
    @Schema(description = "Имя клиента", example = "Иван", required = true)
    private String firstName;

    @NotBlank(message = "Фамилия обязательна для заполнения")
    @Size(min = 2, max = 50, message = "Фамилия должна быть от {min} до {max} символов")
    @Schema(description = "Фамилия клиента", example = "Иванович", required = true)
    private String lastName;


    private String patronymic;
}
