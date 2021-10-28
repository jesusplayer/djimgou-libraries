package com.act.security.core.model.dto.role;

import com.act.core.infra.BaseFilterDto;
import lombok.Data;

import java.util.UUID;

@Data
public class RoleFilterDto extends BaseFilterDto {
    String name;

    String description;

    UUID parentId;
}
