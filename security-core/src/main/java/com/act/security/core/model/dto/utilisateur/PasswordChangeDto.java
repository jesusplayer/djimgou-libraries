package com.act.security.core.model.dto.utilisateur;

import com.act.core.model.IDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PasswordChangeDto implements IDto {
    @NotNull @NotBlank @NotEmpty
    String username;

    @NotNull @NotBlank @NotEmpty
    String password;

    @NotNull @NotBlank @NotEmpty
    String passwordConfirm;

    @NotNull @NotBlank @NotEmpty
    String newPassword;

    @JsonIgnore
    String passwordEnc;
}
