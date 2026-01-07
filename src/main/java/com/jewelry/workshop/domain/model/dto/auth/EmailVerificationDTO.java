package com.jewelry.workshop.domain.model.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "DTO для верификации email")
public class EmailVerificationDTO {

    @NotBlank(message = "Токен верификации обязателен")
    @Schema(description = "Токен верификации email", required = true)
    private String token;
}