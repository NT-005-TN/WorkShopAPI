package com.jewelry.workshop.domain.model.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "DTO для применения скидки к заказу")
public class OrderDiscountDTO {

    @NotNull(message = "Процент скидки обязателен")
    @DecimalMin(value = "0.00", message = "Скидка не может быть отрицательной")
    @DecimalMax(value = "100.00", message = "Скидка не может превышать 100%")
    @Schema(description = "Процент скидки", example = "10.00", required = true)
    private BigDecimal discountPercent;

    @Schema(description = "Причина скидки",
            example = "Акция для постоянных клиентов")
    private String reason;
}