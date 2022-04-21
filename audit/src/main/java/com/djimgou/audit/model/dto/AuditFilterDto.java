package com.djimgou.audit.model.dto;

import com.djimgou.audit.model.AuditAction;
import com.djimgou.core.infra.BaseFilterDto;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class AuditFilterDto extends BaseFilterDto {
    AuditAction action;
    String nomEntite;
    UUID parentId;
    UUID utilisateurId;
    String name;
    String username;
    Date dateDebut;
    Date dateFin;
}
