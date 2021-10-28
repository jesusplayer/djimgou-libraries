/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.act.security.core.tracking.authentication.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

/**
 *
 * @author P-DIT-ABAKACHI
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserRole {
	private Long userId;
	private Long userRole;
	private List<User> userList;
}
