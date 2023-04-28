package com.djimgou.security.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public interface RoleChecker extends InitializingBean {
    /**
     * Check boolean.
     *
     * @param authentication the authentication
     * @param request        the request
     * @return the boolean
     */
    boolean check(Authentication authentication, HttpServletRequest request);
}
