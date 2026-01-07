package com.jewelry.workshop.domain.model.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "DTO для запроса обновления токена")
public class RefreshTokenRequestDTO {

    @NotBlank(message = "Refresh токен обязателен")
    @Schema(description = "Refresh токен", required = true)
    private String refreshToken;
}