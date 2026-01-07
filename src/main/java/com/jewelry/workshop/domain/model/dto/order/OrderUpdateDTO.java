package com.jewelry.workshop.domain.model.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO для обновления заказа")
public class OrderUpdateDTO {

    @Schema(description = "Примечания к заказу", example = "Обновленные примечания")
    private String notes;
}