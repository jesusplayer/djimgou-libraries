package com.djimgou.security.core.model.dto.privilege;

import com.djimgou.core.infra.BaseFilterDto;
import lombok.Data;
import org.springframework.http.HttpMethod;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class PrivilegeFilterDto extends BaseFilterDto {
    @NotNull
    private String code;
    @NotNull
    private String name;

    UUID parentId;

    HttpMethod httpMethod;
}

