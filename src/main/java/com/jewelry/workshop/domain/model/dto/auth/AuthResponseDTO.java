package com.jewelry.workshop.domain.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "DTO для ответа аутентификации")
public class AuthResponseDTO {

    @Schema(description = "Тип токена", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "Access токен")
    private String accessToken;

    @Schema(description = "Refresh токен")
    private String refreshToken;

    @Schema(description = "Время жизни токена в секундах", example = "3600")
    private Long expiresIn;

    @Schema(description = "Информация о пользователе")
    private AuthUserDTO user;
}