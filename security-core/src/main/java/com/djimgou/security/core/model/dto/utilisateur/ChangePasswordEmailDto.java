package com.djimgou.security.core.model.dto.utilisateur;

import lombok.Data;

import javax.validation.constraints.Email;
@Data
public class ChangePasswordEmailDto {
    @Email
    String email;
}
