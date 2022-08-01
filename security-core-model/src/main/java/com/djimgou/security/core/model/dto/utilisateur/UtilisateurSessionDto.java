package com.djimgou.security.core.model.dto.utilisateur;

import com.djimgou.core.cooldto.model.IEntityDto;
import com.djimgou.security.core.model.Utilisateur;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UtilisateurSessionDto extends Utilisateur implements IEntityDto {
   String token;
}
