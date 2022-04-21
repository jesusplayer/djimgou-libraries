package com.djimgou.security.core.model.dto.utilisateur;

import com.djimgou.core.cooldto.model.IDto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserNameChangeDto  implements IDto {
    @NotNull @NotBlank @NotEmpty
    String username;

    @NotNull @NotBlank @NotEmpty
    String newUsername;
}
