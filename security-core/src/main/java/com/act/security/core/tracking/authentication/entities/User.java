package com.act.security.core.tracking.authentication.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {
	private Long id;
	private String ssoId;
	private String password;
	private String firstName;
	private String lastName;
	private String email;
	private List<Role> userRoles;
}
