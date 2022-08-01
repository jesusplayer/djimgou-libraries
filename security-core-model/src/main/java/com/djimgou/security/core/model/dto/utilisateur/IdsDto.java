package com.djimgou.security.core.model.dto.utilisateur;

import com.djimgou.security.core.model.dto.role.IdDto;
import lombok.Data;

import java.util.Set;

@Data
public class IdsDto {
    Set<IdDto> ids;
}
