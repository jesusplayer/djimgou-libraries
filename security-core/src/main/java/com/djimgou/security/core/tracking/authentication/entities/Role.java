package com.djimgou.security.core.tracking.authentication.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Role {
	private Integer roleId;
	private String roleName;
	private String roleDesc;
	private List<Resource> resourceList;
}
