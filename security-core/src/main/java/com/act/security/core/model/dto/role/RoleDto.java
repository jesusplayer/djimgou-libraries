package com.act.security.core.model.dto.role;

import com.act.core.model.IEntityDto;
import com.act.security.core.model.Privilege;
import com.act.security.core.model.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;


@Getter
@Setter
public class RoleDto implements IEntityDto {

    // String name;

    String description;

    UUID parentId;

    String name;

    Set<Privilege> privileges;

    public RoleDto(String code, String url) {
    }

    @Override
    public Class<Role> originalClass() {
        return Role.class;
    }
}
