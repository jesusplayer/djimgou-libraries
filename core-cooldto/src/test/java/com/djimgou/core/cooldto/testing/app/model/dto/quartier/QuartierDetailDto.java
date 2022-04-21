package com.djimgou.core.cooldto.testing.app.model.dto.quartier;

import com.djimgou.core.cooldto.model.IEntityDetailDto;
import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;

public interface QuartierDetailDto extends IEntityDetailDto {
    String getCode();

    String getNom();

    @Value("#{target.ville.id}")
    UUID getVilleId();
}
