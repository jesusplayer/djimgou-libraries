package com.djimgou.core.cooldto.testing.app.model.dto.ville;

import com.djimgou.core.cooldto.annotations.Dto;
import com.djimgou.core.cooldto.annotations.DtoFkId;
import com.djimgou.core.cooldto.testing.app.model.Ville;
import com.djimgou.core.cooldto.model.IEntityDto;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

import static com.djimgou.core.util.AppUtils.has;

@Dto(value = Ville.class)
@Data
public class VilleDto implements IEntityDto {
    @NotBlank(message = "Le code de la ville doit être renseigné")
    String code;

    @NotBlank(message = "Le nom de la ville doit être renseigné")
    String nom;

    @NotNull(message = "L'identifiant de la region de la ville doit être renseigné")

    @DtoFkId(value = "region")
    UUID regionId;

    public boolean hasRegion() {
        return has(regionId);
    }

}
