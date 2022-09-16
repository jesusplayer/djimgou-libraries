package com.djimgou.security.core.model.dto.role;

import com.djimgou.core.cooldto.model.IEntityDto;
import com.djimgou.security.core.model.Privilege;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
public class RoleDto implements IEntityDto {

    // String name;

    String description;

    UUID parentId;

    String name;

    Set<Privilege> privileges;

    public RoleDto(String code, String url) {
    }
}
