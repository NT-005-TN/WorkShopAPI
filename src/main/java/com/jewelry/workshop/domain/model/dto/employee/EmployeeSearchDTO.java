package com.jewelry.workshop.domain.model.dto.employee;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO для поиска сотрудников")
public class EmployeeSearchDTO {

    @Schema(description = "Должность для поиска")
    private String position;

    @Schema(description = "Отдел для поиска")
    private String department;

    @Schema(description = "Роль пользователя", allowableValues = {"SELLER", "ADMIN"})
    private String role;
}