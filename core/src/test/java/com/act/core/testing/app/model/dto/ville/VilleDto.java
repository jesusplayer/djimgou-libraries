package com.act.core.testing.app.model.dto.ville;

import com.act.core.dto.DtoClass;
import com.act.core.dto.DtoFieldDb;
import com.act.core.model.IEntityDto;
import com.act.core.testing.app.model.Ville;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

import static com.act.core.util.AppUtils.has;

@DtoClass(value = Ville.class)
@Data
public class VilleDto implements IEntityDto {
    @NotBlank(message = "Le code de la ville doit être renseigné")
    String code;

    @NotBlank(message = "Le nom de la ville doit être renseigné")
    String nom;

    @NotNull(message = "L'identifiant de la region de la ville doit être renseigné")

    @DtoFieldDb(targetKey = "region")
    UUID regionId;

    public boolean hasRegion() {
        return has(regionId);
    }

}
