package com.jewelry.workshop.domain.model.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@Schema(description = "DTO для запроса изделий с низким остатком")
public class LowStockRequestDTO {

    @Min(value = 1, message = "Пороговое значение должно быть положительным")
    @Schema(description = "Пороговое значение остатка", example = "10", defaultValue = "5")
    private Integer threshold = 5;

    @Schema(description = "Тип изделия для фильтрации", example = "КОЛЬЦО")
    private String productType;

    @Schema(description = "Включить рекомендации по пополнению", example = "true")
    private Boolean includeRecommendations = false;
}