package com.jewelry.workshop.domain.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO для поиска пользователей")
public class UserSearchDTO {

    @Schema(description = "Имя пользователя для поиска")
    private String username;

    @Schema(description = "Email для поиска")
    private String email;

    @Schema(description = "Роль для фильтрации", allowableValues = {"CLIENT", "SELLER", "ADMIN"})
    private String role;

    @Schema(description = "Статус активности", example = "true")
    private Boolean enabled;
}