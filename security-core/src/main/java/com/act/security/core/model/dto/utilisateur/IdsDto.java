package com.act.security.core.model.dto.utilisateur;

import com.act.security.core.model.dto.role.IdDto;
import lombok.Data;

import java.util.Set;

@Data
public class IdsDto {
    Set<IdDto> ids;
}
