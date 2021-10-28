package com.act.security.core.model.dto.privilege;

import com.act.core.infra.BaseFilterDto;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class PrivilegeFilterDto extends BaseFilterDto {
    @NotNull
    private String code;
    @NotNull
    private String name;

    UUID parentId;
}

