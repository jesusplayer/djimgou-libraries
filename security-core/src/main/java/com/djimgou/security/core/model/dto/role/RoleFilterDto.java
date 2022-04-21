package com.djimgou.security.core.model.dto.role;

import com.djimgou.core.infra.BaseFilterDto;
import lombok.Data;

import java.util.UUID;

@Data
public class RoleFilterDto extends BaseFilterDto {
    String name;

    String description;

    UUID parentId;
}
