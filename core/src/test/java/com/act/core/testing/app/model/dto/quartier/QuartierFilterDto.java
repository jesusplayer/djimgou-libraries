package com.act.core.testing.app.model.dto.quartier;

import com.act.core.infra.BaseFilterDto;
import lombok.Data;

import java.util.UUID;

@Data
public class QuartierFilterDto extends BaseFilterDto {
    String code;

    String nom;

    UUID villeId;
}
