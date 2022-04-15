package com.act.security.core.model.dto.utilisateur;

import com.act.core.model.IEntityDto;
import com.act.security.core.model.Role;
import com.act.security.core.model.Utilisateur;
import com.act.security.core.model.dto.role.IdDto;
import com.act.tenantmanager.model.Tenant;
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
public class UtilisateurDto  extends ModifierProfilDto implements IEntityDto {
    @NotNull @NotBlank @NotEmpty
    String password;
    @NotNull @NotBlank @NotEmpty
    String username;
    @NotNull @NotBlank @NotEmpty
    String passwordConfirm;
    String fonction;
    Set<IdDto> authorities;
    Set<IdDto> tenants;
    @JsonIgnore
    private String encodedPasswd;
}
