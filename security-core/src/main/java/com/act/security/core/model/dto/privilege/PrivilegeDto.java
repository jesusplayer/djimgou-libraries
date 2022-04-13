package com.act.security.core.model.dto.privilege;


import com.act.core.model.IEntityDto;
import com.act.security.core.model.Privilege;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class PrivilegeDto implements IEntityDto {
    @NotNull
    private String code;
    @NotNull
    private String name;

    private String description;

    private String url;

    UUID parentId;

    @Override
    public Class<Privilege> originalClass() {
        return Privilege.class;
    }
}
