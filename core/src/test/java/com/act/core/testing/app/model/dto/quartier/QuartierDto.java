package com.act.core.testing.app.model.dto.quartier;

import com.act.core.dto.DtoClass;
import com.act.core.dto.DtoFieldDb;
import com.act.core.model.IEntityDto;
import com.act.core.testing.app.model.Quartier;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

import static com.act.core.util.AppUtils.has;

@DtoClass
@Data
public class QuartierDto implements IEntityDto {
    @NotBlank(message = "Le code du quartier categorie doit être renseigné")
    String code;

    @NotBlank(message = "Le nom du quartier doit être renseigné")
    String nom;

    @NotNull(message = "L'identifiant de la ville du quartier doit être renseigné")
    @DtoFieldDb(targetKey = "ville")
    UUID villeId;

    public boolean hasVille() {
        return has(villeId);
    }
}
