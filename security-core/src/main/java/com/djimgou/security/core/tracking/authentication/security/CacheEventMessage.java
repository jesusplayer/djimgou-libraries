package com.djimgou.security.core.tracking.authentication.security;

import com.djimgou.security.core.model.dto.role.AuthorityDto;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 *BASSANGONEN HERVE LUDOVIC 23.08.2015 ..
 */
@SuppressWarnings("serial")
public class CacheEventMessage extends ApplicationEvent {
	/**
	 * Create a new ApplicationEvent.
	 *
	 * source the object on which the event initially occurred (never {@code null})
	 */
	final List<AuthorityDto> authoritiesDto;

	public CacheEventMessage(Object source, final List<AuthorityDto> authoritiesDto) {
		super(source);
		this.authoritiesDto = authoritiesDto;
	}

	public List<AuthorityDto> getAuthoritiesDto() {
		return authoritiesDto;
	}
}
