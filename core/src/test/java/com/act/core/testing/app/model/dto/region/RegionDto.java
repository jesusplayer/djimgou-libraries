package com.act.core.testing.app.model.dto.region;

import com.act.core.dto.DtoClass;
import com.act.core.model.IEntityDto;
import com.act.core.testing.app.model.Region;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@DtoClass
@Data
public class RegionDto implements IEntityDto {
    @NotBlank(message = "Le code de la region doit être renseigné")
    String code;

    @NotBlank(message = "Le nom de la region doit être renseigné")
    String nom;
}
