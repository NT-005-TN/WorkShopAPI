package com.jewelry.workshop.domain.model.dto.employee;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO для создания сотрудника")
public class EmployeeCreateDTO {

    @NotBlank(message = "Должность обязательна")
    @Size(max = 50, message = "Должность не должна превышать 50 символов")
    @Schema(description = "Должность сотрудника", example = "Продавец-консультант", required = true)
    private String position;

    @NotBlank(message = "Отдел обязателен")
    @Size(max = 50, message = "Отдел не должен превышать 50 символов")
    @Schema(description = "Отдел сотрудника", example = "Продажи", required = true)
    private String department;

    @NotNull(message = "ID пользователя обязательно")
    @Schema(description = "ID пользователя", example = "1", required = true)
    private Long userId;
}