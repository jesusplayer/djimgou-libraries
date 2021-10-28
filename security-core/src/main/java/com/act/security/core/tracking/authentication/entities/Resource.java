package com.act.security.core.tracking.authentication.entities;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Resource {
	private Long id;
	private String name;
	private String description;
	private String rubrique;
	private String url;
}
