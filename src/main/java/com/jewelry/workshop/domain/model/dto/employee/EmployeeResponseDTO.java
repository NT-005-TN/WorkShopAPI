package com.jewelry.workshop.domain.model.dto.employee;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "DTO для ответа с информацией о сотруднике")
public class EmployeeResponseDTO {

    @Schema(description = "ID сотрудника", example = "1")
    private Long id;

    @Schema(description = "ID пользователя", example = "1")
    private Long userId;

    @Schema(description = "Имя пользователя", example = "seller_john")
    private String username;

    @Schema(description = "Email сотрудника", example = "john.seller@example.com")
    private String email;

    @Schema(description = "Роль", example = "SELLER")
    private String role;

    @Schema(description = "Должность", example = "Продавец-консультант")
    private String position;

    @Schema(description = "Отдел", example = "Продажи")
    private String department;

    @Schema(description = "Полная информация", example = "seller_john - Продавец-консультант (Продажи)")
    private String fullInfo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Дата создания", example = "2024-01-15 14:30:00")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Дата обновления", example = "2024-01-15 14:30:00")
    private LocalDateTime updatedAt;
}