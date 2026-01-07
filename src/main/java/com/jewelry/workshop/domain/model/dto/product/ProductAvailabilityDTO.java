package com.jewelry.workshop.domain.model.dto.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "DTO для проверки доступности изделия")
public class ProductAvailabilityDTO {

    @Schema(description = "ID изделия", example = "1")
    private Long productId;

    @Schema(description = "Название изделия", example = "Золотое кольцо с бриллиантом")
    private String productName;

    @Schema(description = "Запрашиваемое количество", example = "2")
    private Integer requestedQuantity;

    @Schema(description = "Доступное количество на складе", example = "10")
    private Integer availableQuantity;

    @Schema(description = "Достаточно ли товара", example = "true")
    private Boolean isAvailable;

    @Schema(description = "Недостающее количество (если не хватает)", example = "0")
    private Integer missingQuantity;

    @Schema(description = "Суммарная цена", example = "90000.00")
    private BigDecimal totalPrice;

    @Schema(description = "Сообщение о доступности",
            example = "Товар доступен в достаточном количестве")
    private String message;
}