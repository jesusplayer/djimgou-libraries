package com.act.security.model.dto.utilisateur;

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

    String password;

    String passwordConfirm;

    String telephone;
}
