package com.act.audit.model.dto;

import com.act.audit.model.AuditAction;
import com.act.core.infra.BaseFilterDto;
import lombok.Data;

import javax.persistence.Transient;
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
