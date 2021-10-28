package com.act.security.core.model.dto.utilisateur;

import com.act.security.core.model.Role;
import com.act.tenantmanager.model.Tenant;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UtilisateurDto  extends ModifierProfilDto{
    @NotNull @NotBlank @NotEmpty
    String password;
    @NotNull @NotBlank @NotEmpty
    String username;
    @NotNull @NotBlank @NotEmpty
    String passwordConfirm;
    String fonction;
    Set<Role> authorities;
    Set<Tenant> tenants;
}
