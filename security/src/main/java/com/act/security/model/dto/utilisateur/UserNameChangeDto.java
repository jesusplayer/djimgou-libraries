package com.act.security.model.dto.utilisateur;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserNameChangeDto {
    @NotNull @NotBlank @NotEmpty
    String username;

    @NotNull @NotBlank @NotEmpty
    String newUsername;
}
