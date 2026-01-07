package com.jewelry.workshop.domain.model.dto.employee;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO для обновления сотрудника")
public class EmployeeUpdateDTO {

    @Size(max = 50, message = "Должность не должна превышать 50 символов")
    @Schema(description = "Должность сотрудника", example = "Старший продавец")
    private String position;

    @Size(max = 50, message = "Отдел не должен превышать 50 символов")
    @Schema(description = "Отдел сотрудника", example = "Управление")
    private String department;
}