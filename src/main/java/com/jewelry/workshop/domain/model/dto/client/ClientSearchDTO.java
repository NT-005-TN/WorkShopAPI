package com.jewelry.workshop.domain.model.dto.client;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO для поиска клиентов")
public class ClientSearchDTO {

    @Schema(description = "Имя для поиска")
    private String firstName;

    @Schema(description = "Фамилия для поиска")
    private String lastName;

    @Schema(description = "Email для поиска")
    private String email;

    @Schema(description = "Телефон для поиска")
    private String phone;

    @Schema(description = "Фильтр по статусу постоянного клиента")
    private Boolean isPermanent;

    @Schema(description = "Минимальное количество заказов")
    private Integer minOrders;

    @Schema(description = "Максимальное количество заказов")
    private Integer maxOrders;

    @Schema(description = "Минимальная сумма всех заказов")
    private Double minTotalSpent;

    @Schema(description = "Дата регистрации с")
    private String createdFrom;

    @Schema(description = "Дата регистрации по")
    private String createdTo;

    @Schema(description = "Сортировка",
            allowableValues = {"name", "created", "orders", "spent"},
            example = "name")
    private String sortBy;

    @Schema(description = "Направление сортировки",
            allowableValues = {"asc", "desc"},
            example = "asc")
    private String sortDirection = "asc";
}