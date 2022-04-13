package com.act.security.core.model.dto.utilisateur;

import com.act.core.model.IEntityDto;
import com.act.security.core.model.Utilisateur;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UtilisateurSessionDto extends Utilisateur implements IEntityDto {
   String token;

   @Override
   public Class<Utilisateur> originalClass() {
      return Utilisateur.class;
   }
}
