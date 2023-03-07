package com.djimgou.security.core.model.dto.utilisateur;

import lombok.Data;

import java.util.UUID;

@Data
public class UsernameDto implements IUsernameDto {
    UUID id;
    String username;
    String email;
}
