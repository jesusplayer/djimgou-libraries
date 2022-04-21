package com.djimgou.core.cooldto.testing.app.model.dto.quartier;

import com.djimgou.core.cooldto.annotations.Dto;
import com.djimgou.core.cooldto.annotations.DtoFkId;
import com.djimgou.core.cooldto.model.IEntityDto;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

import static com.djimgou.core.util.AppUtils.has;

@Dto
@Data
public class QuartierDto implements IEntityDto {
    @NotBlank(message = "Le code du quartier categorie doit être renseigné")
    String code;

    @NotBlank(message = "Le nom du quartier doit être renseigné")
    String nom;

    @NotNull(message = "L'identifiant de la ville du quartier doit être renseigné")
    @DtoFkId(value = "ville")
    UUID villeId;

    public boolean hasVille() {
        return has(villeId);
    }
}
