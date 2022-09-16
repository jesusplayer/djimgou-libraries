package com.djimgou.security.core.model.dto.utilisateur;

import com.djimgou.core.cooldto.model.IEntityDto;
import com.djimgou.security.core.model.dto.role.IdDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ModifierUtilisateurDto extends ModifierProfilDto implements IEntityDto {
    Set<IdDto> authorities;
    Set<IdDto> tenants;
}
