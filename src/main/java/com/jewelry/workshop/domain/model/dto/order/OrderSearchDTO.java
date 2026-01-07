package com.jewelry.workshop.domain.model.dto.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "DTO для поиска заказов (для продавцов/админов)")
public class OrderSearchDTO {

    @Schema(description = "ID клиента", example = "1")
    private Long clientId;

    @Schema(description = "Имя клиента для поиска")
    private String clientName;

    @Schema(description = "Статус заказа", example = "PROCESSING")
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Дата начала периода", example = "2024-01-01")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Дата окончания периода", example = "2024-01-31")
    private LocalDateTime endDate;

    @Schema(description = "Минимальная сумма заказа", example = "10000.00")
    private BigDecimal minAmount;

    @Schema(description = "Максимальная сумма заказа", example = "100000.00")
    private BigDecimal maxAmount;

    @Schema(description = "Номер заказа", example = "1000001")
    private Long orderNumber;

    @Schema(description = "Название товара в заказе")
    private String productName;

    @Schema(description = "Есть ли скидка", example = "true")
    private Boolean hasDiscount;

    @Schema(description = "Сортировка",
            allowableValues = {"date", "amount", "client", "status"},
            example = "date")
    private String sortBy;

    @Schema(description = "Направление сортировки",
            allowableValues = {"asc", "desc"},
            example = "desc")
    private String sortDirection = "desc";

    @Schema(description = "Только завершенные заказы", example = "false")
    private Boolean completedOnly = false;
}