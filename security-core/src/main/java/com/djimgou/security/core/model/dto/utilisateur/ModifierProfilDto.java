package com.djimgou.security.core.model.dto.utilisateur;

import com.djimgou.core.cooldto.model.IDto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ModifierProfilDto implements IDto {
    @NotNull
    UUID id;

    @NotNull @NotBlank @NotEmpty
    String nom;

    @NotNull @NotBlank @NotEmpty
    String prenom;

    @NotNull @NotBlank @NotEmpty
    @Email
    String email;

    String telephone;

    String fonction;
}
