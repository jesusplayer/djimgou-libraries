package com.act.audit.model.dto;

import com.act.audit.model.AuditAction;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
public class AuditDto {
	AuditAction action;
	String nomEntite;
	UUID parentId;
	UUID utilisateurId;
	String name;
	String username;
}
