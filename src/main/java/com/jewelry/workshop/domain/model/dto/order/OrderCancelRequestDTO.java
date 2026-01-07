package com.jewelry.workshop.domain.model.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO для отмены заказа")
public class OrderCancelRequestDTO {

    @NotBlank(message = "Причина отмены обязательна")
    @Size(max = 500, message = "Причина не должна превышать 500 символов")
    @Schema(description = "Причина отмены заказа",
            example = "Передумал, нашел дешевле в другом месте", required = true)
    private String reason;
}