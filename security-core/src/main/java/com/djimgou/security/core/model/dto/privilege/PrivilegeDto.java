package com.djimgou.security.core.model.dto.privilege;


import com.djimgou.core.cooldto.model.IEntityDto;
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

}
