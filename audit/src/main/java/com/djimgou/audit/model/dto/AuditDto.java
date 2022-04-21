package com.djimgou.audit.model.dto;

import com.djimgou.audit.model.AuditAction;
import com.djimgou.core.cooldto.model.IEntityDto;
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
}
