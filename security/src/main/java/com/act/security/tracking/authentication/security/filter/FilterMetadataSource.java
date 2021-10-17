package com.act.security.tracking.authentication.security.filter;

import com.act.security.model.dto.role.AuthorityDto;
import com.act.security.tracking.authentication.security.service.CacheManager;
import com.act.security.tracking.authentication.security.service.ResourceMetaService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * BASSANGONEN HERVE LUDOVIC 23.08.2019 ..
 */
@Log4j2
public class FilterMetadataSource implements FilterInvocationSecurityMetadataSource, InitializingBean {


	@Autowired
	private ResourceMetaService resourceMetaService;

	@Autowired
	private CacheManager cacheManager;

	@Override
	public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
		FilterInvocation fi = (FilterInvocation) object;
		String url = fi.getRequestUrl();
//		log.info("url is 1 {}",  fi.getRequestUrl());

		// to strip of query string from url.

		if (url.indexOf('?') != -1)
			url = url.substring(0, url.indexOf('?'));
		url = url.trim();

		Object principal = null;
		if (SecurityContextHolder.getContext().getAuthentication() == null
				|| SecurityContextHolder.getContext().getAuthentication().getPrincipal() == null) {
			// throw new AuthenticationCredentialsNotFoundException("An Authentication
			// object was not found in the SecurityContext");
			throw new RuntimeException("An Authentication object was not found in the SecurityContext");
			//// List<ConfigAttribute> attributes = new ArrayList<ConfigAttribute>();
			// attributes.add(new SecurityConfig("IS_NOT_AUTHENTICATED_FULLY"));
			// return attributes;
		}

		if (url.equals("/login") || url.startsWith("/resources/") || url.startsWith("/bg") || url.startsWith("/favicon.ico") || (url.equals("/logout"))) {
//			log.info("url is {}",  fi.getRequestUrl());
			return null;
		}
		List<AuthorityDto> userRoleDto = null;
		try {
			userRoleDto = cacheManager.getAuthorities().get(url);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (userRoleDto == null) {
//			log.info("userRoleDto is null {}",   url);
			// throw new AuthenticationCredentialsNotFoundException("An Authentication
			// object was not found in the SecurityContext");
			List<ConfigAttribute> attributes = new ArrayList<ConfigAttribute>();
			attributes.add(new SecurityConfig("IS_NOT_AUTHENTICATED_FULLY"));

			return attributes;
			// return null;
		}
//		log.info("FilterMetaDataSource - getAttributes :  {}",   cacheManager.getAuthorities().get(url).toString());
		List<String> roles = userRoleDto.stream().map(AuthorityDto::getName).collect(Collectors.toList());

		String[] stockArr = new String[roles.size()];
		stockArr = roles.toArray(stockArr);
//		log.info("FilterMetadataSource - getAttributes {}",   stockArr);
//		log.info("FilterMetadataSource valeur de stockArr {}", stockArr);

		return SecurityConfig.createList(stockArr);

	}

	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		// return cacheManager.getAuthorities();
		return null;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return FilterInvocation.class.isAssignableFrom(clazz);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		resourceMetaService.findAllResources();
	}
}