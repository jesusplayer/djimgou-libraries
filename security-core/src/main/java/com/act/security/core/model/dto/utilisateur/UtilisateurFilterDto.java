package com.act.security.core.model.dto.utilisateur;

import com.act.core.infra.BaseFilterDto;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UtilisateurFilterDto extends BaseFilterDto {
    @NotNull
    String nom;

    @NotNull
    String prenom;

    String email;

    String username;

    String telephone;

    String roleName;
}
