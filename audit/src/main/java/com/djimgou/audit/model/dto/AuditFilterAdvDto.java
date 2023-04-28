package com.djimgou.audit.model.dto;

import com.djimgou.audit.model.AuditAction;
import com.djimgou.core.cooldto.annotations.DtoField;
import com.djimgou.core.infra.BaseFilterAdvancedDto;
import com.djimgou.core.infra.BaseFilterDto;
import com.djimgou.core.infra.QueryFieldFilter;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class AuditFilterAdvDto extends BaseFilterAdvancedDto {
    AuditAction action;
    String nomEntite;
    UUID utilisateurId;
    QueryFieldFilter<String> username;
    QueryFieldFilter<String> date;
}
