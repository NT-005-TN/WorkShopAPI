package com.jewelry.workshop.domain.model.dto.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "DTO для фильтрации заказов клиента (/my endpoint)")
public class OrderFilterDTO {

    @Schema(description = "Статус заказа", example = "PROCESSING")
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Дата начала периода", example = "2024-01-01")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Дата окончания периода", example = "2024-01-31")
    private LocalDateTime endDate;

    @Schema(description = "Сортировка",
            allowableValues = {"date", "amount", "status"},
            example = "date")
    private String sortBy;

    @Schema(description = "Направление сортировки",
            allowableValues = {"asc", "desc"},
            example = "desc")
    private String sortDirection = "desc";
}