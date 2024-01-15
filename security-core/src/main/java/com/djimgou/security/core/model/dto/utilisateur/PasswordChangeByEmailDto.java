package com.djimgou.security.core.model.dto.utilisateur;

import com.djimgou.core.cooldto.model.IDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PasswordChangeByEmailDto implements IDto {
    @NotNull @NotBlank @NotEmpty
    String email;

    @NotNull @NotBlank @NotEmpty
    String newPassword;

    @NotNull @NotBlank @NotEmpty
    String passwordConfirm;

    @JsonIgnore
    String passwordEnc;
}
