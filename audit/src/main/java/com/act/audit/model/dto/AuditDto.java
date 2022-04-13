package com.act.audit.model.dto;

import com.act.audit.model.Audit;
import com.act.audit.model.AuditAction;
import com.act.core.model.IEntityDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
public class AuditDto implements IEntityDto {
    AuditAction action;
    String nomEntite;
    UUID parentId;
    UUID utilisateurId;
    String name;
    String username;

    @Override
    public Class<Audit> originalClass() {
        return Audit.class;
    }
}
