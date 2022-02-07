package com.act.security.core.model.dto.role;

import com.act.security.core.model.Privilege;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;


@Getter
@Setter
public class RoleDto {

	// String name;

	String description;

	UUID parentId;

	String name;

	Set<Privilege> privileges;

	public RoleDto(String code, String url) {
	}
}