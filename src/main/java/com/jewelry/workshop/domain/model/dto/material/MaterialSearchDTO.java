package com.jewelry.workshop.domain.model.dto.material;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO для поиска материалов")
public class MaterialSearchDTO {

    @Schema(description = "Название для поиска")
    private String name;

    @Schema(description = "Описание для поиска")
    private String description;
}