package com.djimgou.security;

import com.djimgou.security.core.AppSecurityConfig;
import com.djimgou.security.service.TokenAuthenticationService;
import com.djimgou.session.context.SessionContext;
import com.djimgou.session.enums.SessionKeys;
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

import static com.djimgou.core.util.AppUtils.has;

public class AuthTokenFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;

    private final Boolean tokenEnabled;

    private final AppSecurityConfig appSecurityConfig;

    public AuthTokenFilter(UserDetailsService userDetailsService, Boolean tokenEnabled, AppSecurityConfig appSecurityConfig) {
        this.userDetailsService = userDetailsService;
        this.tokenEnabled = tokenEnabled;
        this.appSecurityConfig = appSecurityConfig;
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader(TokenAuthenticationService.HEADER_STRING);
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7, headerAuth.length());
        }

        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String useToken = request.getHeader("X-Auth-With-Token");
        if (!appSecurityConfig.permitAll() && (has(useToken) || tokenEnabled) ){
            try {
                String jwt = parseJwt(request);
                if (jwt != null && TokenAuthenticationService.validateJwtToken(jwt)) {
                    String username = TokenAuthenticationService.getUserNameFromJwtToken(jwt);

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
                }
            } catch (Exception e) {
                logger.error("Cannot set user authentication: {}", e);
            }
        }
        filterChain.doFilter(request, response);
    }
}
