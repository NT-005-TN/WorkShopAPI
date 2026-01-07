package com.jewelry.workshop.domain.model.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "DTO для обновления остатков изделия")
public class ProductStockUpdateDTO {

    @NotNull(message = "Количество обязательно")
    @Min(value = 1, message = "Количество должно быть положительным")
    @Schema(description = "Количество для добавления/уменьшения", example = "5", required = true)
    private Integer quantity;

    @NotNull(message = "Тип операции обязателен")
    @Schema(description = "Тип операции", example = "INCREASE", required = true,
            allowableValues = {"INCREASE", "DECREASE"})
    private String operationType;
}