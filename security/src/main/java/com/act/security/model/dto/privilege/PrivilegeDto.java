package com.act.security.model.dto.privilege;


import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class PrivilegeDto {
    @NotNull
    private String code;
    @NotNull
    private String name;

    private String description;

    private String url;

    UUID parentId;
}
