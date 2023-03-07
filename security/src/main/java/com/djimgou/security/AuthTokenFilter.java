package com.djimgou.security;

import com.djimgou.audit.model.AuditAction;
import com.djimgou.audit.service.AuditBdService;
import com.djimgou.security.core.AppSecurityConfig;
import com.djimgou.security.service.TokenAuthenticationService;
import com.djimgou.session.context.SessionContext;
import com.djimgou.session.enums.SessionKeys;
import com.djimgou.tenantmanager.aop.TenantContext;
import com.djimgou.tenantmanager.model.Tenant;
import com.djimgou.tenantmanager.service.TenantSessionService;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

import static com.djimgou.core.util.AppUtils.has;

public class AuthTokenFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;

    private final TenantSessionService tenantSessionService;

    private final Boolean tokenEnabled;

    private final AppSecurityConfig appSecurityConfig;


    public AuthTokenFilter(UserDetailsService userDetailsService, TenantSessionService tenantSessionService, Boolean tokenEnabled, AppSecurityConfig appSecurityConfig) {
        this.userDetailsService = userDetailsService;
        this.tenantSessionService = tenantSessionService;
        this.tokenEnabled = tokenEnabled;
        this.appSecurityConfig = appSecurityConfig;

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String useToken = request.getHeader("X-Auth-With-Token");
        if (/*!appSecurityConfig.permitAll() &&*/ (has(useToken) || tokenEnabled)) {
            try {
                String jwt = TokenAuthenticationService.parseJwt(request);
                if (jwt != null && TokenAuthenticationService.validateJwtToken(jwt)) {
                    String username = TokenAuthenticationService.getUserNameFromJwtToken(jwt);
                    Claims payload = TokenAuthenticationService.decodeToken(jwt);
                    final Object o = payload.get(SessionKeys.TENANT_ID);
                    String tenantId = null;
                    if(has(o)){
                        tenantId = (String) o;
                    }

                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    final HttpSession session = request.getSession();
                    SessionContext.setCurrentSessionId(session.getId());
                    SessionContext.setCurrentUsername(username);

                    session.setAttribute(SessionKeys.USERNAME, username);
                    session.setAttribute(SessionKeys.SESSION_ID, session.getId());
                    if(has(tenantId)){
                        Optional<Tenant> tenant = tenantSessionService.putTenant(tenantId);
                        TenantContext.setCurrentTenant(tenant.get());
                    }
                    SessionContext.setCurrentSession(session);

                }
            } catch (Exception e) {
                logger.error("Cannot set user authentication: {}", e);
            }
        }
        filterChain.doFilter(request, response);
    }
}
