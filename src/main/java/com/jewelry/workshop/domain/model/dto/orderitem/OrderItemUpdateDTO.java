package com.jewelry.workshop.domain.model.dto.orderitem;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@Schema(description = "DTO для обновления позиции в заказе")
public class OrderItemUpdateDTO {

    @Min(value = 1, message = "Количество должно быть не менее 1")
    @Schema(description = "Количество товара", example = "3")
    private Integer quantity;
}