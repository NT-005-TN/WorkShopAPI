package com.jewelry.workshop.domain.model.dto.client;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO для обновления статуса клиента (постоянный/непостоянный)")
public class ClientStatusUpdateDTO {

    @NotNull(message = "Статус обязателен")
    @Schema(description = "Является ли клиент постоянным", example = "true", required = true)
    private Boolean isPermanent;

    @Size(max = 500, message = "Комментарий не должен превышать 500 символов")
    @Schema(description = "Комментарий к изменению статуса",
            example = "Клиент сделал 10+ заказов, переводим в постоянные")
    private String comment;
}